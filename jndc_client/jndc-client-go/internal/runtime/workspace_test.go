package runtimecfg

import (
	"os"
	"path/filepath"
	"testing"
)

func TestResolveWorkspaceAndPersistTokens(t *testing.T) {
	t.Parallel()

	workspace, err := ResolveWorkspaceFromHome(t.TempDir())
	if err != nil {
		t.Fatalf("resolve workspace: %v", err)
	}

	if _, err := os.Stat(filepath.Join(workspace.BaseDir, "conf")); err != nil {
		t.Fatalf("conf dir missing: %v", err)
	}

	clientID1, err := workspace.LoadOrCreateClientID()
	if err != nil {
		t.Fatalf("load client id: %v", err)
	}
	clientID2, err := workspace.LoadOrCreateClientID()
	if err != nil {
		t.Fatalf("reload client id: %v", err)
	}
	if clientID1 != clientID2 {
		t.Fatalf("client id mismatch: %s != %s", clientID1, clientID2)
	}
	if len(clientID1) != 32 {
		t.Fatalf("unexpected client id length: %d", len(clientID1))
	}

	authKey1, err := workspace.LoadOrCreateClientAuthKey()
	if err != nil {
		t.Fatalf("load client auth key: %v", err)
	}
	authKey2, err := workspace.LoadOrCreateClientAuthKey()
	if err != nil {
		t.Fatalf("reload client auth key: %v", err)
	}
	if authKey1 != authKey2 {
		t.Fatalf("client auth key mismatch: %s != %s", authKey1, authKey2)
	}
	if len(authKey1) != 64 {
		t.Fatalf("unexpected client auth key length: %d", len(authKey1))
	}
}
