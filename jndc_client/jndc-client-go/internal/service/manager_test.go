package service

import (
	"testing"

	"jndc-client-go/internal/config"
	"jndc-client-go/internal/protocol"
)

type sentMessage struct {
	messageType byte
	payload     any
}

func TestRegisterConfiguredServicesOnlySendsEnabledItems(t *testing.T) {
	t.Parallel()

	cfg := &config.Config{
		Secrete:            "secret",
		Loglevel:           "info",
		ServerIP:           "127.0.0.1",
		ServerPort:         1081,
		AutoReleaseTimeOut: config.DefaultAutoReleaseTimeoutMillis,
		AuthMode:           config.AuthModeSelfManaged,
		ClientServiceDescriptions: []config.ServiceDescription{
			{ServiceName: "enabled", ServiceIP: "127.0.0.1", ServicePort: 8080, ServiceEnable: true},
			{ServiceName: "disabled", ServiceIP: "127.0.0.1", ServicePort: 8081, ServiceEnable: false},
		},
	}

	var sent []sentMessage
	var activated []string
	manager := NewManager(cfg, "client-1", "secret", func(messageType byte, payload any) error {
		sent = append(sent, sentMessage{messageType: messageType, payload: payload})
		return nil
	}, func(service config.ServiceDescription) {
		activated = append(activated, service.ServiceName)
	}, func(service config.ServiceDescription) {})

	if err := manager.RegisterConfiguredServices(); err != nil {
		t.Fatalf("register configured services: %v", err)
	}

	if len(sent) != 1 {
		t.Fatalf("unexpected sent message count: %d", len(sent))
	}
	registration, ok := sent[0].payload.(protocol.RegistrationMessage)
	if !ok {
		t.Fatalf("unexpected payload type: %T", sent[0].payload)
	}
	if len(registration.TCPServiceDescriptions) != 1 {
		t.Fatalf("unexpected register count: %d", len(registration.TCPServiceDescriptions))
	}
	if registration.TCPServiceDescriptions[0].ServiceName != "enabled" {
		t.Fatalf("unexpected service name: %s", registration.TCPServiceDescriptions[0].ServiceName)
	}
	if len(activated) != 1 || activated[0] != "enabled" {
		t.Fatalf("unexpected activations: %#v", activated)
	}
}

func TestApplyControlledSyncDiffsServices(t *testing.T) {
	t.Parallel()

	cfg := &config.Config{Secrete: "secret"}
	var sent []sentMessage
	var activated []string
	var deactivated []string

	manager := NewManager(cfg, "client-1", "secret", func(messageType byte, payload any) error {
		sent = append(sent, sentMessage{messageType: messageType, payload: payload})
		return nil
	}, func(service config.ServiceDescription) {
		activated = append(activated, service.ServiceName)
	}, func(service config.ServiceDescription) {
		deactivated = append(deactivated, service.ServiceName)
	})

	if err := manager.StartRegister([]config.ServiceDescription{
		{ServiceName: "svc-a", ServiceIP: "127.0.0.1", ServicePort: 8080, Description: "a", ServiceEnable: true},
		{ServiceName: "svc-b", ServiceIP: "127.0.0.1", ServicePort: 8081, Description: "b", ServiceEnable: true},
	}); err != nil {
		t.Fatalf("seed register: %v", err)
	}

	sent = nil
	activated = nil
	deactivated = nil

	err := manager.ApplyControlledSync(protocol.ServiceControlMessage{
		ClientID: "client-1",
		TCPServiceDescriptions: []protocol.TcpServiceDescription{
			{ServiceName: "svc-b", ServiceIP: "127.0.0.1", ServicePort: 8081, Description: "b"},
			{ServiceName: "svc-c", ServiceIP: "127.0.0.1", ServicePort: 8082, Description: "c"},
		},
	}, true)
	if err != nil {
		t.Fatalf("apply controlled sync: %v", err)
	}

	if len(deactivated) != 1 || deactivated[0] != "svc-a" {
		t.Fatalf("unexpected deactivations: %#v", deactivated)
	}
	if len(activated) != 1 || activated[0] != "svc-c" {
		t.Fatalf("unexpected activations: %#v", activated)
	}
	if len(sent) != 3 {
		t.Fatalf("unexpected message count: %d", len(sent))
	}

	if sent[0].messageType != protocol.ServiceUnregister {
		t.Fatalf("first message should unregister, got %d", sent[0].messageType)
	}
	if sent[1].messageType != protocol.ServiceRegister {
		t.Fatalf("second message should register add, got %d", sent[1].messageType)
	}
	if sent[2].messageType != protocol.ServiceRegister {
		t.Fatalf("third message should reregister, got %d", sent[2].messageType)
	}
}
