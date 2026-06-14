package forwarder

import (
	"bytes"
	"context"
	"errors"
	"io"
	"log/slog"
	"net"
	"strconv"
	"sync"
	"time"

	"jndc-client-go/internal/config"
	"jndc-client-go/internal/protocol"
)

const (
	maxPendingBufferCount = 128
	maxPendingBufferBytes = 1024 * 1024
	dialTimeout           = 10 * time.Second
)

type MessageSender func(message *protocol.Message) error

type Manager struct {
	logger      *slog.Logger
	send        MessageSender
	autoRelease time.Duration
	mu          sync.RWMutex
	providers   map[string]*provider
}

func NewManager(logger *slog.Logger, send MessageSender, autoReleaseTimeoutMillis int64) *Manager {
	manager := &Manager{
		logger:      logger,
		send:        send,
		autoRelease: time.Duration(autoReleaseTimeoutMillis) * time.Millisecond,
		providers:   make(map[string]*provider),
	}
	go manager.timeoutLoop()
	return manager
}

func (m *Manager) EnsureService(service config.ServiceDescription) {
	key, err := service.ResolvedUniqueTag()
	if err != nil {
		m.logger.Error("resolve service failed", "service", service.UniqueTag(), "err", err)
		return
	}
	resolvedIP, _ := service.ResolveIPv4()

	m.mu.Lock()
	defer m.mu.Unlock()

	if existing, exists := m.providers[key]; exists {
		existing.updateService(service, resolvedIP)
		return
	}
	m.providers[key] = newProvider(m.logger, service, resolvedIP, m.send, m.autoRelease)
}

func (m *Manager) RemoveService(service config.ServiceDescription) {
	key, err := service.ResolvedUniqueTag()
	if err != nil {
		m.logger.Error("resolve service failed", "service", service.UniqueTag(), "err", err)
		return
	}

	m.mu.Lock()
	provider := m.providers[key]
	delete(m.providers, key)
	m.mu.Unlock()

	if provider != nil {
		provider.closeAll(true)
	}
}

func (m *Manager) HandleMessage(message *protocol.Message) error {
	key := providerKeyFromMessage(message)
	m.mu.RLock()
	provider := m.providers[key]
	m.mu.RUnlock()
	if provider == nil {
		return errors.New("service provider not found: " + key)
	}
	provider.handleMessage(message)
	return nil
}

func (m *Manager) HandleInterrupted(message *protocol.Message) {
	key := providerKeyFromMessage(message)
	m.mu.RLock()
	provider := m.providers[key]
	m.mu.RUnlock()
	if provider == nil {
		return
	}
	provider.handleInterrupted(message)
}

func (m *Manager) CloseAllConnections() {
	m.mu.RLock()
	defer m.mu.RUnlock()
	for _, provider := range m.providers {
		provider.closeAll(false)
	}
}

func (m *Manager) timeoutLoop() {
	ticker := time.NewTicker(30 * time.Second)
	defer ticker.Stop()

	for range ticker.C {
		m.mu.RLock()
		providers := make([]*provider, 0, len(m.providers))
		for _, provider := range m.providers {
			providers = append(providers, provider)
		}
		m.mu.RUnlock()
		for _, provider := range providers {
			provider.releaseTimedOut()
		}
	}
}

func providerKeyFromMessage(message *protocol.Message) string {
	return net.JoinHostPort(message.LocalIP.String(), strconv.Itoa(message.LocalPort))
}

type provider struct {
	logger      *slog.Logger
	send        MessageSender
	autoRelease time.Duration

	mu         sync.Mutex
	service    config.ServiceDescription
	resolvedIP net.IP
	conns      map[string]*localConnection
}

func newProvider(logger *slog.Logger, service config.ServiceDescription, resolvedIP net.IP, send MessageSender, autoRelease time.Duration) *provider {
	return &provider{
		logger:      logger,
		send:        send,
		autoRelease: autoRelease,
		service:     service.Clone(),
		resolvedIP:  append(net.IP(nil), resolvedIP...),
		conns:       make(map[string]*localConnection),
	}
}

func (p *provider) updateService(service config.ServiceDescription, resolvedIP net.IP) {
	p.mu.Lock()
	defer p.mu.Unlock()
	p.service = service.Clone()
	p.resolvedIP = append(net.IP(nil), resolvedIP...)
}

func (p *provider) handleMessage(message *protocol.Message) {
	remoteKey := remoteKey(message.RemoteIP, message.RemotePort)

	p.mu.Lock()
	connection := p.conns[remoteKey]
	if connection == nil {
		connection = newLocalConnection(p, remoteKey, message)
		p.conns[remoteKey] = connection
	}
	p.mu.Unlock()

	if bytes.Equal(message.Data, protocol.ActiveMessage) {
		connection.ensureDial(nil)
		return
	}
	connection.ensureDial(message.Data)
}

func (p *provider) handleInterrupted(message *protocol.Message) {
	remoteKey := remoteKey(message.RemoteIP, message.RemotePort)
	p.mu.Lock()
	connection := p.conns[remoteKey]
	p.mu.Unlock()
	if connection != nil {
		connection.release(false)
	}
}

func (p *provider) removeConnection(remoteKey string, connection *localConnection) {
	p.mu.Lock()
	defer p.mu.Unlock()
	if current := p.conns[remoteKey]; current == connection {
		delete(p.conns, remoteKey)
	}
}

func (p *provider) closeAll(notifyRemote bool) {
	p.mu.Lock()
	connections := make([]*localConnection, 0, len(p.conns))
	for _, connection := range p.conns {
		connections = append(connections, connection)
	}
	p.mu.Unlock()
	for _, connection := range connections {
		connection.release(notifyRemote)
	}
}

func (p *provider) releaseTimedOut() {
	p.mu.Lock()
	connections := make([]*localConnection, 0, len(p.conns))
	for _, connection := range p.conns {
		connections = append(connections, connection)
	}
	p.mu.Unlock()
	for _, connection := range connections {
		if connection.isTimedOut() {
			p.logger.Info("release local forward connection because timeout", "remoteKey", connection.remoteKey)
			connection.release(true)
		}
	}
}

type localConnection struct {
	provider  *provider
	remoteKey string
	model     *protocol.Message

	mu            sync.Mutex
	conn          net.Conn
	dialing       bool
	flushing      bool
	released      bool
	interruptSent bool
	lastActive    time.Time
	pending       [][]byte
	pendingBytes  int
}

func newLocalConnection(provider *provider, remoteKey string, message *protocol.Message) *localConnection {
	return &localConnection{
		provider:   provider,
		remoteKey:  remoteKey,
		model:      message.Clone(),
		lastActive: time.Now(),
	}
}

func (c *localConnection) ensureDial(initialData []byte) {
	c.mu.Lock()
	if c.released {
		c.mu.Unlock()
		return
	}
	c.lastActive = time.Now()
	if len(initialData) > 0 {
		if !c.bufferLocked(initialData) {
			c.mu.Unlock()
			c.notifyInterrupted()
			c.release(false)
			return
		}
	}
	if c.conn != nil || c.dialing {
		shouldFlush := c.conn != nil && len(c.pending) > 0
		c.mu.Unlock()
		if shouldFlush {
			c.flushPending()
		}
		return
	}
	c.dialing = true
	c.mu.Unlock()

	go c.dial()
}

func (c *localConnection) dial() {
	address := net.JoinHostPort(c.provider.resolvedIP.String(), strconv.Itoa(c.provider.service.ServicePort.Int()))
	conn, err := (&net.Dialer{Timeout: dialTimeout}).DialContext(context.Background(), "tcp", address)
	if err != nil {
		c.provider.logger.Error("connect to local service failed", "address", address, "err", err)
		c.mu.Lock()
		c.dialing = false
		c.mu.Unlock()
		c.notifyInterrupted()
		c.release(false)
		return
	}

	c.mu.Lock()
	if c.released {
		c.mu.Unlock()
		_ = conn.Close()
		return
	}
	c.conn = conn
	c.dialing = false
	c.lastActive = time.Now()
	c.mu.Unlock()

	c.flushPending()
	go c.readLoop()
}

func (c *localConnection) flushPending() {
	for {
		c.mu.Lock()
		if c.conn == nil || c.released || len(c.pending) == 0 {
			c.mu.Unlock()
			return
		}
		if c.flushing {
			c.mu.Unlock()
			return
		}

		c.flushing = true
		conn := c.conn
		pending := c.pending
		c.pending = nil
		c.pendingBytes = 0
		c.mu.Unlock()

		for _, chunk := range pending {
			if _, err := conn.Write(chunk); err != nil {
				c.provider.logger.Error("write pending buffer failed", "remoteKey", c.remoteKey, "err", err)
				c.mu.Lock()
				c.flushing = false
				c.mu.Unlock()
				c.notifyInterrupted()
				c.release(false)
				return
			}
		}

		c.mu.Lock()
		c.flushing = false
		shouldContinue := c.conn == conn && !c.released && len(c.pending) > 0
		c.mu.Unlock()
		if !shouldContinue {
			return
		}
	}
}

func (c *localConnection) readLoop() {
	buffer := make([]byte, 32*1024)
	for {
		n, err := c.conn.Read(buffer)
		if n > 0 {
			c.mu.Lock()
			c.lastActive = time.Now()
			c.mu.Unlock()

			message := c.model.Clone()
			message.Type = protocol.TCPData
			message.Data = append([]byte(nil), buffer[:n]...)
			if sendErr := c.provider.send(message); sendErr != nil {
				c.provider.logger.Warn("send local response to tunnel failed", "remoteKey", c.remoteKey, "err", sendErr)
			}
		}
		if err != nil {
			if !errors.Is(err, io.EOF) {
				c.provider.logger.Debug("local connection read ended", "remoteKey", c.remoteKey, "err", err)
			}
			c.notifyInterrupted()
			c.release(false)
			return
		}
	}
}

func (c *localConnection) bufferLocked(data []byte) bool {
	nextBytes := c.pendingBytes + len(data)
	if len(c.pending) >= maxPendingBufferCount || nextBytes > maxPendingBufferBytes {
		return false
	}
	c.pending = append(c.pending, append([]byte(nil), data...))
	c.pendingBytes = nextBytes
	return true
}

func (c *localConnection) notifyInterrupted() {
	c.mu.Lock()
	if c.interruptSent || c.released {
		c.mu.Unlock()
		return
	}
	c.interruptSent = true
	model := c.model.Clone()
	c.mu.Unlock()

	model.Type = protocol.ConnectionInterrupted
	model.Data = append([]byte(nil), protocol.Blank...)
	if err := c.provider.send(model); err != nil {
		c.provider.logger.Debug("send connection interrupted failed", "remoteKey", c.remoteKey, "err", err)
	}
}

func (c *localConnection) release(notifyRemote bool) {
	c.mu.Lock()
	if c.released {
		c.mu.Unlock()
		return
	}
	c.released = true
	conn := c.conn
	c.conn = nil
	c.pending = nil
	c.pendingBytes = 0
	c.mu.Unlock()

	if notifyRemote {
		c.notifyInterrupted()
	}
	if conn != nil {
		_ = conn.Close()
	}
	c.provider.removeConnection(c.remoteKey, c)
}

func (c *localConnection) isTimedOut() bool {
	c.mu.Lock()
	defer c.mu.Unlock()
	return c.conn != nil && !c.released && time.Since(c.lastActive) > c.provider.autoRelease
}

func remoteKey(ip net.IP, port int) string {
	return net.JoinHostPort(ip.String(), strconv.Itoa(port))
}
