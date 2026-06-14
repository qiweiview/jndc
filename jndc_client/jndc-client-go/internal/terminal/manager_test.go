package terminal

import (
	"testing"
	"time"

	"log/slog"

	"jndc-client-go/internal/protocol"
)

func TestTerminalOpenInputAndClose(t *testing.T) {
	t.Parallel()

	messages := make(chan protocol.TerminalControlMessage, 16)
	manager := NewManagerWithFactory(
		slog.Default(),
		t.TempDir(),
		func(message protocol.TerminalControlMessage) error {
			messages <- message
			return nil
		},
		DebugCommandFactory(t.TempDir(), "/bin/sh", "-c", "cat"),
	)

	manager.Handle(protocol.TerminalControlMessage{
		Action:    protocol.TerminalActionOpen,
		SessionID: "session-1",
		ClientID:  "client-1",
	})

	openMessage := waitForMessage(t, messages, protocol.TerminalActionOpen)
	if openMessage.SessionID != "session-1" {
		t.Fatalf("unexpected open session id: %s", openMessage.SessionID)
	}

	manager.Handle(protocol.TerminalControlMessage{
		Action:    protocol.TerminalActionInput,
		SessionID: "session-1",
		ClientID:  "client-1",
		Data:      "hello-terminal\n",
	})

	output := waitForMessage(t, messages, protocol.TerminalActionOutput)
	if output.Data != "hello-terminal\r\n" && output.Data != "hello-terminal\n" {
		t.Fatalf("unexpected output: %q", output.Data)
	}

	manager.Handle(protocol.TerminalControlMessage{
		Action:    protocol.TerminalActionResize,
		SessionID: "session-1",
		ClientID:  "client-1",
		Cols:      intPtr(140),
		Rows:      intPtr(40),
	})

	manager.Handle(protocol.TerminalControlMessage{
		Action:    protocol.TerminalActionClose,
		SessionID: "session-1",
		ClientID:  "client-1",
	})

	exitMessage := waitForMessage(t, messages, protocol.TerminalActionExit)
	if exitMessage.ExitCode == nil {
		t.Fatal("expected exit code")
	}
}

func waitForMessage(t *testing.T, messages <-chan protocol.TerminalControlMessage, action string) protocol.TerminalControlMessage {
	t.Helper()
	timeout := time.After(5 * time.Second)
	for {
		select {
		case message := <-messages:
			if message.Action == action {
				return message
			}
		case <-timeout:
			t.Fatalf("timeout waiting for terminal action %s", action)
		}
	}
}

func intPtr(value int) *int {
	return &value
}
