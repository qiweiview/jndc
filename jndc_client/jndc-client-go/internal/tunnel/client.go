package tunnel

import (
	"context"
	"errors"
	"fmt"
	"io"
	"log/slog"
	"net"
	"strings"
	"sync"
	"time"

	"jndc-client-go/internal/config"
	secret "jndc-client-go/internal/crypto"
	"jndc-client-go/internal/device"
	"jndc-client-go/internal/forwarder"
	"jndc-client-go/internal/protocol"
	runtimecfg "jndc-client-go/internal/runtime"
	"jndc-client-go/internal/service"
	"jndc-client-go/internal/terminal"
)

const reconnectDelay = 5 * time.Second

type Client struct {
	logger        *slog.Logger
	workspace     *runtimecfg.Workspace
	cfg           *config.Config
	clientID      string
	clientAuthKey string
	cipher        *secret.Cipher
	localIP       net.IP

	forwarder      *forwarder.Manager
	serviceManager *service.Manager
	terminal       *terminal.Manager

	mu      sync.RWMutex
	session *session
}

type session struct {
	conn    net.Conn
	writeCh chan *protocol.Message
	done    chan struct{}
}

type permanentError struct {
	err error
}

func (e *permanentError) Error() string {
	return e.err.Error()
}

func (e *permanentError) Unwrap() error {
	return e.err
}

func NewClient(
	logger *slog.Logger,
	workspace *runtimecfg.Workspace,
	cfg *config.Config,
	clientID string,
	clientAuthKey string,
) (*Client, error) {
	localIP, err := resolveLocalIPv4(cfg.ServerIP.String())
	if err != nil {
		localIP = append(net.IP(nil), protocol.LocalStubIP...)
	}

	client := &Client{
		logger:        logger,
		workspace:     workspace,
		cfg:           cfg,
		clientID:      clientID,
		clientAuthKey: clientAuthKey,
		cipher:        secret.New(cfg.Secrete.String()),
		localIP:       localIP,
	}

	client.forwarder = forwarder.NewManager(logger, client.sendTunnelMessage, cfg.AutoReleaseTimeOut.Int64())
	client.serviceManager = service.NewManager(
		cfg,
		clientID,
		cfg.Secrete.String(),
		client.sendTypedPayload,
		client.forwarder.EnsureService,
		client.forwarder.RemoveService,
	)
	client.terminal = terminal.NewManager(logger, workspace.BaseDir, client.sendTerminalMessage)
	return client, nil
}

func (c *Client) Run(ctx context.Context) error {
	for {
		select {
		case <-ctx.Done():
			c.closeSession()
			c.forwarder.CloseAllConnections()
			c.terminal.CloseForDisconnect()
			return nil
		default:
		}

		err := c.connectAndServe(ctx)
		if err == nil {
			return nil
		}

		var permanent *permanentError
		if errors.As(err, &permanent) {
			return permanent.err
		}

		c.logger.Warn("tunnel disconnected, will retry", "err", err)
		c.forwarder.CloseAllConnections()
		c.terminal.CloseForDisconnect()

		select {
		case <-ctx.Done():
			return nil
		case <-time.After(reconnectDelay):
		}
	}
}

func (c *Client) connectAndServe(ctx context.Context) error {
	address := c.cfg.ResolveServerAddress()
	conn, err := (&net.Dialer{Timeout: 10 * time.Second}).DialContext(ctx, "tcp", address)
	if err != nil {
		return err
	}
	c.logger.Info("connected to jndc server", "address", address)

	session := &session{
		conn:    conn,
		writeCh: make(chan *protocol.Message, 128),
		done:    make(chan struct{}),
	}
	c.setSession(session)
	defer c.closeSession()

	writerErrCh := make(chan error, 1)
	go c.writerLoop(session, writerErrCh)

	if err := c.sendOpenChannel(); err != nil {
		return err
	}

	heartbeatStarted := false
	forceControlledRegisterSync := false

	for {
		message, err := protocol.ReadMessage(conn)
		if err != nil {
			if errors.Is(err, io.EOF) {
				c.logger.Warn("server closed tunnel connection")
				return fmt.Errorf("server closed connection")
			}
			c.logger.Warn("read tunnel message failed", "err", err)
			return err
		}
		if message.Type == protocol.NoAccess {
			return &permanentError{err: errors.New("connection rejected: invalid secret or access denied")}
		}

		plaintext, err := c.cipher.Decode(message.Data)
		if err != nil {
			return &permanentError{err: fmt.Errorf("decrypt message failed: %w", err)}
		}
		message.Data = plaintext
		c.logger.Debug("received tunnel message", "type", fmt.Sprintf("0x%02x", message.Type), "localPort", message.LocalPort, "serverPort", message.ServerPort, "remotePort", message.RemotePort, "dataLen", len(message.Data))

		switch message.Type {
		case protocol.ChannelHeartbeat:
			c.logger.Debug("received heartbeat from server")
		case protocol.TCPData, protocol.TCPActive:
			if err := c.forwarder.HandleMessage(message); err != nil {
				c.logger.Error("forward message failed", "type", message.Type, "err", err)
				copy := message.Clone()
				copy.Type = protocol.ConnectionInterrupted
				copy.Data = append([]byte(nil), protocol.Blank...)
				_ = c.sendTunnelMessage(copy)
			}
		case protocol.OpenChannel:
			var openResponse protocol.OpenChannelMessage
			if err := message.UnmarshalPayload(&openResponse); err != nil {
				return err
			}
			if !heartbeatStarted {
				heartbeatStarted = true
				go c.heartbeatLoop(session.done)
			}
			if c.cfg.AuthMode.Int() == config.AuthModeFullAuthorized {
				forceControlledRegisterSync = true
				c.logger.Info("full authorized mode, waiting for service control sync")
			} else if err := c.serviceManager.RegisterConfiguredServices(); err != nil {
				return err
			}
		case protocol.ServiceRegister:
			c.logger.Warn("unexpected service register message from server")
		case protocol.ServiceUnregister:
			c.logger.Info("service unregister acknowledged")
		case protocol.ServiceControlSync:
			var syncMessage protocol.ServiceControlMessage
			if err := message.UnmarshalPayload(&syncMessage); err != nil {
				return err
			}
			c.logger.Info("received service control sync", "clientId", syncMessage.ClientID, "serviceCount", len(syncMessage.TCPServiceDescriptions))
			if err := c.serviceManager.ApplyControlledSync(syncMessage, forceControlledRegisterSync); err != nil {
				return err
			}
			forceControlledRegisterSync = false
		case protocol.TerminalControl:
			var terminalMessage protocol.TerminalControlMessage
			if err := message.UnmarshalPayload(&terminalMessage); err != nil {
				return err
			}
			c.logger.Info("received terminal control", "action", terminalMessage.Action, "sessionId", terminalMessage.SessionID)
			c.terminal.Handle(terminalMessage)
		case protocol.ConnectionInterrupted:
			c.forwarder.HandleInterrupted(message)
		case protocol.UserErrorType:
			var userError protocol.UserError
			if err := message.UnmarshalPayload(&userError); err != nil {
				c.logger.Error("server user error", "raw", string(message.Data))
				break
			}
			if strings.HasPrefix(userError.Description, "Ip Address Rule") {
				return &permanentError{err: errors.New(userError.Description)}
			}
			c.logger.Error("server user error", "code", userError.Code, "description", userError.Description)
		case protocol.UncatchableError:
			c.logger.Error("server uncatchable error", "message", string(message.Data))
		default:
			c.logger.Warn("received unknown message type", "type", message.Type)
		}

		select {
		case err := <-writerErrCh:
			return err
		default:
		}
	}
}

func (c *Client) sendOpenChannel() error {
	openChannelMessage := protocol.OpenChannelMessage{
		Auth:          c.cfg.Secrete.String(),
		ChannelID:     c.clientID,
		ClientAuthKey: c.clientAuthKey,
		AuthMode:      c.cfg.AuthMode.Int(),
		DeviceSummary: device.Collect(c.logger, c.workspace.BaseDir),
	}
	return c.sendTypedPayload(protocol.OpenChannel, openChannelMessage)
}

func (c *Client) heartbeatLoop(done <-chan struct{}) {
	ticker := time.NewTicker(60 * time.Second)
	defer ticker.Stop()

	message := protocol.OpenChannelMessage{
		ChannelID: c.clientID,
	}
	for {
		select {
		case <-done:
			return
		case <-ticker.C:
			if err := c.sendTypedPayload(protocol.ChannelHeartbeat, message); err != nil {
				c.logger.Debug("send heartbeat failed", "err", err)
			}
		}
	}
}

func (c *Client) sendTypedPayload(messageType byte, payload any) error {
	message := protocol.NewMessage(c.localIP, c.localIP, protocol.UnusedPort, protocol.UnusedPort, protocol.UnusedPort, messageType)
	if err := message.MarshalPayload(payload); err != nil {
		return err
	}
	return c.sendTunnelMessage(message)
}

func (c *Client) sendTerminalMessage(message protocol.TerminalControlMessage) error {
	return c.sendTypedPayload(protocol.TerminalControl, message)
}

func (c *Client) sendTunnelMessage(message *protocol.Message) error {
	c.mu.RLock()
	session := c.session
	c.mu.RUnlock()
	if session == nil {
		return errors.New("tunnel is not connected")
	}

	select {
	case session.writeCh <- message.CloneWithData():
		return nil
	case <-session.done:
		return errors.New("tunnel session closed")
	default:
		session.writeCh <- message.CloneWithData()
		return nil
	}
}

func (c *Client) writerLoop(session *session, errCh chan<- error) {
	defer close(session.done)

	for message := range session.writeCh {
		encrypted, err := c.cipher.Encode(message.Data)
		if err != nil {
			errCh <- err
			return
		}
		frame := message.Clone()
		frame.Data = encrypted
		if err := protocol.WriteMessage(session.conn, frame); err != nil {
			errCh <- err
			return
		}
	}
}

func (c *Client) setSession(session *session) {
	c.mu.Lock()
	defer c.mu.Unlock()
	c.session = session
}

func (c *Client) closeSession() {
	c.mu.Lock()
	session := c.session
	if session == nil {
		c.mu.Unlock()
		return
	}
	c.session = nil
	close(session.writeCh)
	c.mu.Unlock()

	_ = session.conn.Close()
	<-session.done
}

func resolveLocalIPv4(serverHost string) (net.IP, error) {
	if parsed := net.ParseIP(serverHost); parsed != nil {
		if ip4 := parsed.To4(); ip4 != nil {
			return ip4, nil
		}
	}
	conn, err := net.Dial("udp", net.JoinHostPort(serverHost, "80"))
	if err != nil {
		return nil, err
	}
	defer conn.Close()
	localAddr, ok := conn.LocalAddr().(*net.UDPAddr)
	if !ok || localAddr.IP == nil {
		return nil, errors.New("cannot resolve local ip")
	}
	if ip4 := localAddr.IP.To4(); ip4 != nil {
		return ip4, nil
	}
	return nil, errors.New("local ip is not ipv4")
}
