package runtimecfg

import (
	"crypto/rand"
	"encoding/hex"
	"fmt"
	"os"
	"path/filepath"
	"strings"
)

const (
	jndcHomeDir = ".jndc"
	clientDir   = "client"
	confDir     = "conf"
)

type Workspace struct {
	BaseDir      string
	ConfDir      string
	ConfigPath   string
	ClientIDPath string
	AuthKeyPath  string
}

func ResolveWorkspace() (*Workspace, error) {
	home, err := os.UserHomeDir()
	if err != nil {
		return nil, err
	}
	return ResolveWorkspaceFromHome(home)
}

func ResolveWorkspaceFromHome(home string) (*Workspace, error) {
	baseDir := filepath.Join(home, jndcHomeDir, clientDir)
	workspace := &Workspace{
		BaseDir:      baseDir,
		ConfDir:      filepath.Join(baseDir, confDir),
		ConfigPath:   filepath.Join(baseDir, confDir, "config.yml"),
		ClientIDPath: filepath.Join(baseDir, confDir, "client_id"),
		AuthKeyPath:  filepath.Join(baseDir, confDir, "client_auth_key"),
	}
	if err := workspace.ensure(); err != nil {
		return nil, err
	}
	return workspace, nil
}

func (w *Workspace) ResolveConfigPath(candidate string) string {
	if strings.HasSuffix(candidate, ".yml") || strings.HasSuffix(candidate, ".yaml") {
		return candidate
	}
	return w.ConfigPath
}

func (w *Workspace) LoadOrCreateClientID() (string, error) {
	return w.loadOrCreateToken(w.ClientIDPath, 16)
}

func (w *Workspace) LoadOrCreateClientAuthKey() (string, error) {
	return w.loadOrCreateToken(w.AuthKeyPath, 32)
}

func (w *Workspace) ensure() error {
	for _, path := range []string{w.BaseDir, w.ConfDir} {
		if err := os.MkdirAll(path, 0o755); err != nil {
			return err
		}
	}
	return nil
}

func (w *Workspace) loadOrCreateToken(path string, bytesLen int) (string, error) {
	if content, err := os.ReadFile(path); err == nil {
		value := strings.TrimSpace(string(content))
		if value != "" {
			return value, nil
		}
	} else if !os.IsNotExist(err) {
		return "", err
	}

	raw := make([]byte, bytesLen)
	if _, err := rand.Read(raw); err != nil {
		return "", err
	}
	value := hex.EncodeToString(raw)
	if err := os.WriteFile(path, []byte(value), 0o644); err != nil {
		return "", fmt.Errorf("write %s: %w", path, err)
	}
	return value, nil
}
