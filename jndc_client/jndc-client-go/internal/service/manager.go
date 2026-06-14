package service

import (
	"fmt"
	"net"
	"slices"
	"strconv"
	"strings"
	"sync"

	"jndc-client-go/internal/config"
	"jndc-client-go/internal/protocol"
)

type MessageSender func(messageType byte, payload any) error
type ServiceActivator func(service config.ServiceDescription)
type ServiceDeactivator func(service config.ServiceDescription)

type Manager struct {
	mu           sync.RWMutex
	clientID     string
	auth         string
	cfg          *config.Config
	send         MessageSender
	onActivate   ServiceActivator
	onDeactivate ServiceDeactivator
	active       map[string]config.ServiceDescription
}

func NewManager(
	cfg *config.Config,
	clientID string,
	auth string,
	send MessageSender,
	onActivate ServiceActivator,
	onDeactivate ServiceDeactivator,
) *Manager {
	return &Manager{
		clientID:     clientID,
		auth:         auth,
		cfg:          cfg,
		send:         send,
		onActivate:   onActivate,
		onDeactivate: onDeactivate,
		active:       make(map[string]config.ServiceDescription),
	}
}

func (m *Manager) RegisterConfiguredServices() error {
	return m.StartRegister(m.cfg.EnabledServices())
}

func (m *Manager) StartRegister(services []config.ServiceDescription) error {
	services = m.normalizeServices(services)
	if len(services) == 0 {
		return nil
	}

	m.mu.Lock()
	defer m.mu.Unlock()

	registerList := make([]protocol.TcpServiceDescription, 0, len(services))
	for _, service := range services {
		key, err := service.ResolvedUniqueTag()
		if err != nil {
			return err
		}
		if _, exists := m.active[key]; !exists && m.onActivate != nil {
			m.onActivate(service)
		}
		m.active[key] = service.Clone()
		registerList = append(registerList, toTCPServiceDescription(service))
	}
	return m.sendRegistrationLocked(protocol.ServiceRegister, registrationTypeRegister, registerList)
}

func (m *Manager) StopRegister(services []config.ServiceDescription) error {
	services = m.normalizeServices(services)
	if len(services) == 0 {
		return nil
	}

	m.mu.Lock()
	defer m.mu.Unlock()

	unregisterList := make([]protocol.TcpServiceDescription, 0, len(services))
	for _, service := range services {
		key, err := service.ResolvedUniqueTag()
		if err != nil {
			return err
		}
		if existing, exists := m.active[key]; exists {
			if m.onDeactivate != nil {
				m.onDeactivate(existing)
			}
			delete(m.active, key)
		}
		unregisterList = append(unregisterList, toTCPServiceDescription(service))
	}
	return m.sendRegistrationLocked(protocol.ServiceUnregister, registrationTypeUnregister, unregisterList)
}

func (m *Manager) ApplyControlledSync(message protocol.ServiceControlMessage, forceReRegister bool) error {
	targetServices, err := fromTCPServiceDescriptions(message.TCPServiceDescriptions)
	if err != nil {
		return err
	}

	m.mu.RLock()
	currentServices := make(map[string]config.ServiceDescription, len(m.active))
	for key, value := range m.active {
		currentServices[key] = value.Clone()
	}
	m.mu.RUnlock()

	targetMap := make(map[string]config.ServiceDescription, len(targetServices))
	for _, service := range targetServices {
		key, err := service.ResolvedUniqueTag()
		if err != nil {
			return err
		}
		targetMap[key] = service.Clone()
	}

	toRemove := make([]config.ServiceDescription, 0)
	toAdd := make([]config.ServiceDescription, 0)
	toReRegister := make([]config.ServiceDescription, 0)

	for key, current := range currentServices {
		target, exists := targetMap[key]
		if !exists || serviceChanged(current, target) {
			toRemove = append(toRemove, current)
			continue
		}
		if forceReRegister {
			toReRegister = append(toReRegister, current)
		}
	}

	for key, target := range targetMap {
		current, exists := currentServices[key]
		if !exists || serviceChanged(current, target) {
			toAdd = append(toAdd, target)
		}
	}

	if err := m.StopRegister(toRemove); err != nil {
		return err
	}
	if err := m.StartRegister(toAdd); err != nil {
		return err
	}
	if err := m.ReRegister(toReRegister); err != nil {
		return err
	}
	return nil
}

func (m *Manager) ReRegister(services []config.ServiceDescription) error {
	services = m.normalizeServices(services)
	if len(services) == 0 {
		return nil
	}

	m.mu.RLock()
	defer m.mu.RUnlock()

	registerList := make([]protocol.TcpServiceDescription, 0, len(services))
	for _, service := range services {
		registerList = append(registerList, toTCPServiceDescription(service))
	}
	return m.sendRegistrationLocked(protocol.ServiceRegister, registrationTypeRegister, registerList)
}

func (m *Manager) ActiveServices() []config.ServiceDescription {
	m.mu.RLock()
	defer m.mu.RUnlock()
	services := make([]config.ServiceDescription, 0, len(m.active))
	for _, value := range m.active {
		services = append(services, value.Clone())
	}
	slices.SortFunc(services, func(left, right config.ServiceDescription) int {
		return strings.Compare(left.UniqueTag(), right.UniqueTag())
	})
	return services
}

func (m *Manager) sendRegistrationLocked(messageType byte, registerType byte, services []protocol.TcpServiceDescription) error {
	if len(services) == 0 {
		return nil
	}
	message := protocol.RegistrationMessage{
		Type:                   registerType,
		Auth:                   m.auth,
		TCPServiceDescriptions: services,
		ChannelID:              m.clientID,
	}
	return m.send(messageType, message)
}

func (m *Manager) normalizeServices(services []config.ServiceDescription) []config.ServiceDescription {
	result := make([]config.ServiceDescription, 0, len(services))
	for _, service := range services {
		if !service.ServiceEnable {
			continue
		}
		result = append(result, service.Clone())
	}
	return result
}

func toTCPServiceDescription(service config.ServiceDescription) protocol.TcpServiceDescription {
	return protocol.TcpServiceDescription{
		ID:          service.ID,
		ServicePort: service.ServicePort.Int(),
		ServiceIP:   service.ServiceIP,
		ServiceName: service.ServiceName,
		Description: service.EffectiveDescription(),
	}
}

func fromTCPServiceDescriptions(services []protocol.TcpServiceDescription) ([]config.ServiceDescription, error) {
	result := make([]config.ServiceDescription, 0, len(services))
	for _, service := range services {
		description := config.ServiceDescription{
			ID:            service.ID,
			ServicePort:   config.IntValue(service.ServicePort),
			ServiceIP:     service.ServiceIP,
			ServiceName:   service.ServiceName,
			Description:   service.Description,
			ServiceEnable: true,
		}
		if err := description.Validate(); err != nil {
			return nil, fmt.Errorf("invalid service %s:%d: %w", service.ServiceIP, service.ServicePort, err)
		}
		result = append(result, description)
	}
	return result, nil
}

func serviceChanged(left, right config.ServiceDescription) bool {
	return left.ServiceName != right.ServiceName ||
		left.Description != right.Description ||
		left.ServiceIP != right.ServiceIP ||
		left.ServicePort.Int() != right.ServicePort.Int()
}

const (
	registrationTypeRegister   = 0x00
	registrationTypeUnregister = 0x01
)

func RemoteKey(ip net.IP, port int) string {
	return net.JoinHostPort(ip.String(), strconv.Itoa(port))
}
