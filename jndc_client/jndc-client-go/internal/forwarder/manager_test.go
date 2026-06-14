package forwarder

import (
	"io"
	"log/slog"
	"net"
	"testing"
	"time"

	"jndc-client-go/internal/config"
	"jndc-client-go/internal/protocol"
)

func TestLocalConnectionFlushesDataAfterDial(t *testing.T) {
	listener, err := net.Listen("tcp", "127.0.0.1:0")
	if err != nil {
		t.Fatalf("listen: %v", err)
	}
	defer listener.Close()

	receivedCh := make(chan []byte, 1)
	errCh := make(chan error, 1)
	go func() {
		conn, acceptErr := listener.Accept()
		if acceptErr != nil {
			errCh <- acceptErr
			return
		}
		defer conn.Close()

		_ = conn.SetReadDeadline(time.Now().Add(2 * time.Second))
		data, readErr := io.ReadAll(io.LimitReader(conn, int64(len("firstsecond"))))
		if readErr != nil {
			errCh <- readErr
			return
		}
		receivedCh <- data
	}()

	addr := listener.Addr().(*net.TCPAddr)
	service := config.ServiceDescription{
		ServiceName:   "mysql",
		ServiceIP:     "127.0.0.1",
		ServicePort:   config.IntValue(addr.Port),
		ServiceEnable: true,
	}

	provider := newProvider(
		slog.New(slog.NewTextHandler(io.Discard, nil)),
		service,
		net.ParseIP("127.0.0.1").To4(),
		func(message *protocol.Message) error { return nil },
		time.Minute,
	)

	model := protocol.NewMessage(
		net.ParseIP("127.0.0.1").To4(),
		net.ParseIP("127.0.0.1").To4(),
		12345,
		9099,
		3306,
		protocol.TCPData,
	)

	connection := newLocalConnection(provider, "127.0.0.1:12345", model)
	connection.ensureDial([]byte("first"))

	deadline := time.Now().Add(2 * time.Second)
	for {
		connection.mu.Lock()
		connected := connection.conn != nil
		connection.mu.Unlock()
		if connected {
			break
		}
		if time.Now().After(deadline) {
			t.Fatal("local connection was not established")
		}
		time.Sleep(10 * time.Millisecond)
	}

	connection.ensureDial([]byte("second"))

	select {
	case data := <-receivedCh:
		if got, want := string(data), "firstsecond"; got != want {
			t.Fatalf("unexpected forwarded data: got %q want %q", got, want)
		}
	case acceptErr := <-errCh:
		t.Fatalf("listener error: %v", acceptErr)
	case <-time.After(3 * time.Second):
		t.Fatal("timed out waiting for forwarded data")
	}

	connection.release(false)
}
