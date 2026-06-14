package terminal

import (
	"fmt"
	"io"
	"log/slog"
	"os"
	"os/exec"
	"strings"
	"sync"
	"syscall"

	"github.com/creack/pty"

	"jndc-client-go/internal/protocol"
)

type MessageSender func(message protocol.TerminalControlMessage) error
type CommandFactory func(workspaceDir string) *exec.Cmd

type Manager struct {
	logger       *slog.Logger
	workspaceDir string
	send         MessageSender
	factory      CommandFactory

	mu      sync.Mutex
	session *session
}

func NewManager(logger *slog.Logger, workspaceDir string, send MessageSender) *Manager {
	return NewManagerWithFactory(logger, workspaceDir, send, defaultCommandFactory)
}

func NewManagerWithFactory(logger *slog.Logger, workspaceDir string, send MessageSender, factory CommandFactory) *Manager {
	return &Manager{
		logger:       logger,
		workspaceDir: workspaceDir,
		send:         send,
		factory:      factory,
	}
}

func (m *Manager) Handle(message protocol.TerminalControlMessage) {
	m.logger.Info("handle terminal control", "action", message.Action, "sessionId", message.SessionID)
	switch message.Action {
	case protocol.TerminalActionOpen:
		m.open(message)
	case protocol.TerminalActionInput:
		m.input(message)
	case protocol.TerminalActionResize:
		m.resize(message)
	case protocol.TerminalActionClose:
		m.close(message.SessionID, false)
	}
}

func (m *Manager) CloseForDisconnect() {
	m.mu.Lock()
	current := m.session
	m.mu.Unlock()
	if current != nil {
		m.close(current.sessionID, true)
	}
}

func (m *Manager) open(message protocol.TerminalControlMessage) {
	m.mu.Lock()
	if m.session != nil {
		m.mu.Unlock()
		m.sendError(message.SessionID, message.ClientID, "terminal session already active")
		return
	}
	cmd := m.factory(m.workspaceDir)
	if cmd == nil {
		m.mu.Unlock()
		m.sendError(message.SessionID, message.ClientID, "terminal command factory returned nil")
		return
	}
	size := &pty.Winsize{Cols: uint16(valueOrDefault(message.Cols, 120)), Rows: uint16(valueOrDefault(message.Rows, 32))}
	tty, err := pty.StartWithSize(cmd, size)
	if err != nil {
		m.mu.Unlock()
		m.sendError(message.SessionID, message.ClientID, "open terminal failed: "+err.Error())
		return
	}
	current := &session{
		sessionID: message.SessionID,
		clientID:  message.ClientID,
		shellType: cmd.Path,
		cmd:       cmd,
		tty:       tty,
		manager:   m,
	}
	m.session = current
	m.mu.Unlock()

	go current.readLoop()
	go current.waitLoop()
	m.sendMessage(protocol.TerminalControlMessage{
		Action:    protocol.TerminalActionOpen,
		SessionID: message.SessionID,
		ClientID:  message.ClientID,
		ShellType: cmd.Path,
	})
}

func (m *Manager) input(message protocol.TerminalControlMessage) {
	current := m.requireSession(message.SessionID, message.ClientID)
	if current == nil {
		return
	}
	if _, err := current.tty.Write([]byte(message.Data)); err != nil {
		m.sendError(message.SessionID, message.ClientID, "write terminal input failed: "+err.Error())
		m.close(message.SessionID, true)
	}
}

func (m *Manager) resize(message protocol.TerminalControlMessage) {
	current := m.requireSession(message.SessionID, message.ClientID)
	if current == nil {
		return
	}
	size := &pty.Winsize{Cols: uint16(valueOrDefault(message.Cols, 120)), Rows: uint16(valueOrDefault(message.Rows, 32))}
	if err := pty.Setsize(current.tty, size); err != nil {
		m.logger.Warn("resize terminal failed", "sessionId", message.SessionID, "err", err)
	}
}

func (m *Manager) close(sessionID string, silent bool) {
	m.mu.Lock()
	current := m.session
	if current == nil || current.sessionID != sessionID {
		m.mu.Unlock()
		return
	}
	m.session = nil
	current.closed = true
	current.silent = silent
	m.mu.Unlock()

	if current.tty != nil {
		_ = current.tty.Close()
	}
	if current.cmd != nil && current.cmd.Process != nil {
		_ = current.cmd.Process.Kill()
	}
}

func (m *Manager) onOutput(current *session, data string) {
	m.mu.Lock()
	active := m.session == current
	m.mu.Unlock()
	if !active || data == "" {
		return
	}
	m.sendMessage(protocol.TerminalControlMessage{
		Action:    protocol.TerminalActionOutput,
		SessionID: current.sessionID,
		ClientID:  current.clientID,
		Data:      data,
	})
}

func (m *Manager) onExit(current *session, exitCode int) {
	m.mu.Lock()
	if m.session == current {
		m.session = nil
	}
	silent := current.silent
	m.mu.Unlock()
	if silent {
		return
	}
	m.sendMessage(protocol.TerminalControlMessage{
		Action:    protocol.TerminalActionExit,
		SessionID: current.sessionID,
		ClientID:  current.clientID,
		ExitCode:  &exitCode,
	})
}

func (m *Manager) onError(current *session, err error) {
	m.mu.Lock()
	if m.session == current {
		m.session = nil
	}
	silent := current.silent
	m.mu.Unlock()
	if silent {
		return
	}
	m.sendError(current.sessionID, current.clientID, err.Error())
}

func (m *Manager) requireSession(sessionID, clientID string) *session {
	m.mu.Lock()
	defer m.mu.Unlock()
	if m.session == nil {
		m.sendError(sessionID, clientID, "terminal session not found")
		return nil
	}
	if m.session.sessionID != sessionID {
		m.sendError(sessionID, clientID, "terminal session not found")
		return nil
	}
	return m.session
}

func (m *Manager) sendError(sessionID, clientID, message string) {
	m.sendMessage(protocol.TerminalControlMessage{
		Action:    protocol.TerminalActionError,
		SessionID: sessionID,
		ClientID:  clientID,
		Message:   message,
	})
}

func (m *Manager) sendMessage(message protocol.TerminalControlMessage) {
	if err := m.send(message); err != nil {
		m.logger.Debug("send terminal message failed", "action", message.Action, "err", err)
	}
}

type session struct {
	sessionID string
	clientID  string
	shellType string
	cmd       *exec.Cmd
	tty       *os.File
	manager   *Manager

	closed bool
	silent bool
}

func (s *session) readLoop() {
	buffer := make([]byte, 4096)
	for {
		n, err := s.tty.Read(buffer)
		if n > 0 {
			s.manager.onOutput(s, string(buffer[:n]))
		}
		if err != nil {
			if err != io.EOF && !s.closed {
				s.manager.onError(s, err)
			}
			return
		}
	}
}

func (s *session) waitLoop() {
	err := s.cmd.Wait()
	exitCode := 0
	if err != nil {
		if exitError, ok := err.(*exec.ExitError); ok {
			if status, ok := exitError.Sys().(syscall.WaitStatus); ok {
				exitCode = status.ExitStatus()
			} else {
				exitCode = exitError.ExitCode()
			}
		} else if !s.closed {
			s.manager.onError(s, err)
			return
		}
	}
	s.manager.onExit(s, exitCode)
}

func defaultCommandFactory(workspaceDir string) *exec.Cmd {
	shell := strings.TrimSpace(os.Getenv("SHELL"))
	if shell == "" {
		shell = "/bin/sh"
	}
	cmd := exec.Command(shell)
	cmd.Dir = workspaceDir
	cmd.Env = append(os.Environ(), "TERM=xterm-256color")
	return cmd
}

func valueOrDefault(value *int, fallback int) int {
	if value == nil || *value <= 0 {
		return fallback
	}
	return *value
}

func DebugCommandFactory(workspaceDir string, command string, args ...string) CommandFactory {
	return func(_ string) *exec.Cmd {
		cmd := exec.Command(command, args...)
		cmd.Dir = workspaceDir
		cmd.Env = append(os.Environ(), "TERM=xterm-256color")
		return cmd
	}
}

func (m *Manager) String() string {
	m.mu.Lock()
	defer m.mu.Unlock()
	if m.session == nil {
		return "terminal<idle>"
	}
	return fmt.Sprintf("terminal<session=%s>", m.session.sessionID)
}
