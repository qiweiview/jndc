package main

import (
	"context"
	"log/slog"
	"os"
	"os/signal"
	"syscall"

	"jndc-client-go/internal/config"
	runtimecfg "jndc-client-go/internal/runtime"
	"jndc-client-go/internal/tunnel"
)

func main() {
	ctx, cancel := signal.NotifyContext(context.Background(), os.Interrupt, syscall.SIGTERM)
	defer cancel()

	configPath := ""
	if len(os.Args) > 1 {
		configPath = os.Args[1]
	}

	workspace, err := runtimecfg.ResolveWorkspace()
	if err != nil {
		slog.Error("resolve workspace failed", "err", err)
		os.Exit(1)
	}

	cfg, err := config.Load(workspace.ResolveConfigPath(configPath))
	if err != nil {
		slog.Error("load config failed", "err", err)
		os.Exit(1)
	}

	logger := config.NewLogger(cfg.Loglevel.String())
	slog.SetDefault(logger)

	clientID, err := workspace.LoadOrCreateClientID()
	if err != nil {
		logger.Error("load client id failed", "err", err)
		os.Exit(1)
	}
	clientAuthKey, err := workspace.LoadOrCreateClientAuthKey()
	if err != nil {
		logger.Error("load client auth key failed", "err", err)
		os.Exit(1)
	}

	client, err := tunnel.NewClient(logger, workspace, cfg, clientID, clientAuthKey)
	if err != nil {
		logger.Error("create client failed", "err", err)
		os.Exit(1)
	}

	if err := client.Run(ctx); err != nil {
		logger.Error("client exited with error", "err", err)
		os.Exit(1)
	}
}
