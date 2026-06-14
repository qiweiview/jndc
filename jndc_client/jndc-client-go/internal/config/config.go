package config

import (
	"errors"
	"fmt"
	"log/slog"
	"net"
	"os"
	"strconv"
	"strings"

	"gopkg.in/yaml.v3"
)

const (
	AuthModeSelfManaged             = 0
	AuthModeFullAuthorized          = 1
	DefaultAutoReleaseTimeoutMillis = 10 * 60 * 1000
)

type IntValue int

func (i *IntValue) UnmarshalYAML(node *yaml.Node) error {
	value, err := parseIntNode(node)
	if err != nil {
		return err
	}
	*i = IntValue(value)
	return nil
}

func (i IntValue) Int() int {
	return int(i)
}

type Int64Value int64

func (i *Int64Value) UnmarshalYAML(node *yaml.Node) error {
	value, err := parseIntNode(node)
	if err != nil {
		return err
	}
	*i = Int64Value(value)
	return nil
}

func (i Int64Value) Int64() int64 {
	return int64(i)
}

type StringValue string

func (s StringValue) String() string {
	return string(s)
}

type Config struct {
	Secrete                   StringValue          `yaml:"secrete"`
	Loglevel                  StringValue          `yaml:"loglevel"`
	ServerIP                  StringValue          `yaml:"serverIp"`
	ServerPort                IntValue             `yaml:"serverPort"`
	AutoReleaseTimeOut        Int64Value           `yaml:"autoReleaseTimeOut"`
	AuthMode                  IntValue             `yaml:"authMode"`
	ClientServiceDescriptions []ServiceDescription `yaml:"clientServiceDescriptions"`
	ManageConfig              map[string]any       `yaml:"manageConfig"`
}

type ServiceDescription struct {
	ID            string   `yaml:"id" json:"id,omitempty"`
	ServicePort   IntValue `yaml:"servicePort" json:"servicePort"`
	ServiceIP     string   `yaml:"serviceIp" json:"serviceIp"`
	ServiceName   string   `yaml:"serviceName" json:"serviceName"`
	Description   string   `yaml:"description" json:"description"`
	ServiceEnable bool     `yaml:"serviceEnable" json:"-"`
}

func Load(path string) (*Config, error) {
	data, err := os.ReadFile(path)
	if err != nil {
		return nil, err
	}

	var cfg Config
	if err := yaml.Unmarshal(data, &cfg); err != nil {
		return nil, err
	}

	if err := cfg.Validate(); err != nil {
		return nil, err
	}
	return &cfg, nil
}

func (c *Config) Validate() error {
	if strings.TrimSpace(c.Secrete.String()) == "" {
		return errors.New("secrete is required")
	}
	if strings.TrimSpace(c.Loglevel.String()) == "" {
		c.Loglevel = "info"
	}
	if strings.TrimSpace(c.ServerIP.String()) == "" {
		return errors.New("serverIp is required")
	}
	if c.ServerPort.Int() <= 0 {
		return fmt.Errorf("serverPort must be positive: %d", c.ServerPort.Int())
	}
	if c.AutoReleaseTimeOut.Int64() <= 0 {
		c.AutoReleaseTimeOut = DefaultAutoReleaseTimeoutMillis
	}
	if c.AuthMode.Int() != AuthModeSelfManaged && c.AuthMode.Int() != AuthModeFullAuthorized {
		return fmt.Errorf("unsupported authMode: %d", c.AuthMode.Int())
	}
	for idx := range c.ClientServiceDescriptions {
		service := &c.ClientServiceDescriptions[idx]
		if !service.ServiceEnable {
			continue
		}
		if err := service.Validate(); err != nil {
			return fmt.Errorf("clientServiceDescriptions[%d]: %w", idx, err)
		}
	}
	return nil
}

func (c *Config) ResolveServerAddress() string {
	return net.JoinHostPort(c.ServerIP.String(), strconv.Itoa(c.ServerPort.Int()))
}

func (c *Config) EnabledServices() []ServiceDescription {
	services := make([]ServiceDescription, 0, len(c.ClientServiceDescriptions))
	for _, service := range c.ClientServiceDescriptions {
		if service.ServiceEnable {
			services = append(services, service.Clone())
		}
	}
	return services
}

func (s ServiceDescription) Validate() error {
	if strings.TrimSpace(s.ServiceName) == "" {
		return errors.New("serviceName is required")
	}
	if strings.TrimSpace(s.ServiceIP) == "" {
		return errors.New("serviceIp is required")
	}
	if s.ServicePort.Int() <= 0 {
		return fmt.Errorf("servicePort must be positive: %d", s.ServicePort.Int())
	}
	if _, err := resolveIPv4(s.ServiceIP); err != nil {
		return fmt.Errorf("resolve serviceIp failed: %w", err)
	}
	return nil
}

func (s ServiceDescription) UniqueTag() string {
	return net.JoinHostPort(s.ServiceIP, strconv.Itoa(s.ServicePort.Int()))
}

func (s ServiceDescription) ResolvedUniqueTag() (string, error) {
	ip, err := resolveIPv4(s.ServiceIP)
	if err != nil {
		return "", err
	}
	return net.JoinHostPort(ip.String(), strconv.Itoa(s.ServicePort.Int())), nil
}

func (s ServiceDescription) ResolveIPv4() (net.IP, error) {
	return resolveIPv4(s.ServiceIP)
}

func (s ServiceDescription) Clone() ServiceDescription {
	return ServiceDescription{
		ID:            s.ID,
		ServicePort:   s.ServicePort,
		ServiceIP:     s.ServiceIP,
		ServiceName:   s.ServiceName,
		Description:   s.Description,
		ServiceEnable: s.ServiceEnable,
	}
}

func (s ServiceDescription) EffectiveDescription() string {
	if strings.TrimSpace(s.Description) != "" {
		return s.Description
	}
	return s.ServiceName
}

func NewLogger(level string) *slog.Logger {
	var slogLevel slog.Level
	switch strings.ToLower(strings.TrimSpace(level)) {
	case "debug":
		slogLevel = slog.LevelDebug
	case "warn":
		slogLevel = slog.LevelWarn
	case "error":
		slogLevel = slog.LevelError
	default:
		slogLevel = slog.LevelInfo
	}
	return slog.New(slog.NewTextHandler(os.Stdout, &slog.HandlerOptions{Level: slogLevel}))
}

func parseIntNode(node *yaml.Node) (int64, error) {
	switch node.Kind {
	case yaml.ScalarNode:
		value := strings.TrimSpace(node.Value)
		if value == "" {
			return 0, nil
		}
		parsed, err := strconv.ParseInt(value, 10, 64)
		if err != nil {
			return 0, fmt.Errorf("parse int value %q: %w", value, err)
		}
		return parsed, nil
	default:
		return 0, fmt.Errorf("unsupported yaml node kind: %d", node.Kind)
	}
}

func resolveIPv4(host string) (net.IP, error) {
	if ip := net.ParseIP(host); ip != nil {
		ip4 := ip.To4()
		if ip4 == nil {
			return nil, fmt.Errorf("ipv6 is not supported: %s", host)
		}
		return ip4, nil
	}

	ips, err := net.LookupIP(host)
	if err != nil {
		return nil, err
	}
	for _, ip := range ips {
		if ip4 := ip.To4(); ip4 != nil {
			return ip4, nil
		}
	}
	return nil, fmt.Errorf("no ipv4 address found for %s", host)
}
