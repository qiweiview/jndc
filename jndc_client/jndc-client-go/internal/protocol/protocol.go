package protocol

import (
	"bytes"
	"encoding/binary"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"net"
)

const (
	TCPData               byte = 0x01
	TCPActive             byte = 0x02
	ServiceRegister       byte = 0x03
	ServiceUnregister     byte = 0x04
	ConnectionInterrupted byte = 0x05
	NoAccess              byte = 0x06
	UserErrorType         byte = 0x07
	UncatchableError      byte = 0x08
	ChannelHeartbeat      byte = 0x09
	OpenChannel           byte = 0x0A
	ServiceControlSync    byte = 0x0B
	TerminalControl       byte = 0x0C

	UnusedPort       = 0
	AutoUnpackLength = 5 * 1024 * 1024
	FixLength        = 29
)

var (
	Magic         = []byte("NDC")
	Blank         = []byte("BLANK")
	ActiveMessage = []byte("ACTIVE_MESSAGE")
	LocalStubIP   = net.IPv4(127, 0, 0, 1)
)

type Message struct {
	Version    byte
	Type       byte
	LocalIP    net.IP
	RemoteIP   net.IP
	LocalPort  int
	ServerPort int
	RemotePort int
	Data       []byte
}

type RegistrationMessage struct {
	Type                   byte                    `json:"type"`
	Auth                   string                  `json:"auth"`
	TCPServiceDescriptions []TcpServiceDescription `json:"tcpServiceDescriptions"`
	Message                string                  `json:"message"`
	ChannelID              string                  `json:"channelId"`
}

type OpenChannelMessage struct {
	Auth          string        `json:"auth"`
	ChannelID     string        `json:"channelId"`
	ClientAuthKey string        `json:"clientAuthKey"`
	AuthMode      int           `json:"authMode"`
	DeviceSummary DeviceSummary `json:"deviceSummary"`
}

type ServiceControlMessage struct {
	ClientID               string                  `json:"clientId"`
	TCPServiceDescriptions []TcpServiceDescription `json:"tcpServiceDescriptions"`
}

type TerminalControlMessage struct {
	Action    string `json:"action"`
	SessionID string `json:"sessionId"`
	ClientID  string `json:"clientId"`
	Data      string `json:"data"`
	Cols      *int   `json:"cols"`
	Rows      *int   `json:"rows"`
	ExitCode  *int   `json:"exitCode"`
	Message   string `json:"message"`
	ShellType string `json:"shellType"`
}

const (
	TerminalActionOpen   = "OPEN"
	TerminalActionInput  = "INPUT"
	TerminalActionOutput = "OUTPUT"
	TerminalActionClose  = "CLOSE"
	TerminalActionExit   = "EXIT"
	TerminalActionError  = "ERROR"
	TerminalActionResize = "RESIZE"
)

type TcpServiceDescription struct {
	ID          string `json:"id"`
	ServicePort int    `json:"servicePort"`
	ServiceIP   string `json:"serviceIp"`
	ServiceName string `json:"serviceName"`
	Description string `json:"description"`
}

type UserError struct {
	Code        int    `json:"code"`
	Description string `json:"description"`
}

type DeviceSummary struct {
	OSName           string   `json:"osName"`
	OSVersion        string   `json:"osVersion"`
	CPUModel         string   `json:"cpuModel"`
	CPULogicalCores  int      `json:"cpuLogicalCores"`
	GPUNames         []string `json:"gpuNames"`
	MemoryTotalBytes int64    `json:"memoryTotalBytes"`
	DiskTotalBytes   int64    `json:"diskTotalBytes"`
	DiskFreeBytes    int64    `json:"diskFreeBytes"`
}

func NewMessage(remoteIP, localIP net.IP, remotePort, serverPort, localPort int, typ byte) *Message {
	return &Message{
		Version:    1,
		Type:       typ,
		LocalIP:    normalizeIPv4(localIP),
		RemoteIP:   normalizeIPv4(remoteIP),
		LocalPort:  localPort,
		ServerPort: serverPort,
		RemotePort: remotePort,
		Data:       []byte{},
	}
}

func (m *Message) Clone() *Message {
	return &Message{
		Version:    m.Version,
		Type:       m.Type,
		LocalIP:    append(net.IP(nil), m.LocalIP...),
		RemoteIP:   append(net.IP(nil), m.RemoteIP...),
		LocalPort:  m.LocalPort,
		ServerPort: m.ServerPort,
		RemotePort: m.RemotePort,
		Data:       []byte{},
	}
}

func (m *Message) CloneWithData() *Message {
	clone := m.Clone()
	clone.Data = append([]byte(nil), m.Data...)
	return clone
}

func (m *Message) MarshalPayload(payload any) error {
	data, err := json.Marshal(payload)
	if err != nil {
		return err
	}
	m.Data = data
	return nil
}

func (m *Message) UnmarshalPayload(target any) error {
	if len(m.Data) == 0 {
		return errors.New("empty payload")
	}
	return json.Unmarshal(m.Data, target)
}

func (m *Message) Encode() ([][]byte, error) {
	chunks := splitBytes(m.Data, AutoUnpackLength)
	frames := make([][]byte, 0, len(chunks))
	for _, chunk := range chunks {
		frame, err := encodeFrame(m, chunk)
		if err != nil {
			return nil, err
		}
		frames = append(frames, frame)
	}
	return frames, nil
}

func WriteMessage(w io.Writer, m *Message) error {
	frames, err := m.Encode()
	if err != nil {
		return err
	}
	for _, frame := range frames {
		if _, err := w.Write(frame); err != nil {
			return err
		}
	}
	return nil
}

func ReadMessage(r io.Reader) (*Message, error) {
	header := make([]byte, FixLength)
	if _, err := io.ReadFull(r, header); err != nil {
		return nil, err
	}
	message, dataSize, err := ParseFixedHeader(header)
	if err != nil {
		return nil, err
	}
	if dataSize == 0 {
		return message, nil
	}
	data := make([]byte, dataSize)
	if _, err := io.ReadFull(r, data); err != nil {
		return nil, err
	}
	message.Data = data
	return message, nil
}

func ParseFixedHeader(header []byte) (*Message, int, error) {
	if len(header) < FixLength {
		return nil, 0, fmt.Errorf("header too short: %d", len(header))
	}
	if !bytes.Equal(Magic, header[:3]) {
		return nil, 0, errors.New("unsupported protocol")
	}
	dataSize := int(binary.BigEndian.Uint32(header[25:29]))
	if dataSize < 0 || dataSize > AutoUnpackLength {
		return nil, 0, fmt.Errorf("invalid data length: %d", dataSize)
	}
	return &Message{
		Version:    header[3],
		Type:       header[4],
		LocalIP:    net.IPv4(header[5], header[6], header[7], header[8]),
		RemoteIP:   net.IPv4(header[9], header[10], header[11], header[12]),
		LocalPort:  int(binary.BigEndian.Uint32(header[13:17])),
		ServerPort: int(binary.BigEndian.Uint32(header[17:21])),
		RemotePort: int(binary.BigEndian.Uint32(header[21:25])),
		Data:       []byte{},
	}, dataSize, nil
}

func encodeFrame(message *Message, data []byte) ([]byte, error) {
	if len(data) > AutoUnpackLength {
		return nil, fmt.Errorf("data too large: %d", len(data))
	}
	localIP := normalizeIPv4(message.LocalIP)
	remoteIP := normalizeIPv4(message.RemoteIP)
	frame := make([]byte, FixLength+len(data))
	copy(frame[0:3], Magic)
	frame[3] = message.Version
	frame[4] = message.Type
	copy(frame[5:9], localIP.To4())
	copy(frame[9:13], remoteIP.To4())
	binary.BigEndian.PutUint32(frame[13:17], uint32(message.LocalPort))
	binary.BigEndian.PutUint32(frame[17:21], uint32(message.ServerPort))
	binary.BigEndian.PutUint32(frame[21:25], uint32(message.RemotePort))
	binary.BigEndian.PutUint32(frame[25:29], uint32(len(data)))
	copy(frame[29:], data)
	return frame, nil
}

func splitBytes(data []byte, limit int) [][]byte {
	if len(data) == 0 {
		return [][]byte{{}}
	}
	if len(data) <= limit {
		return [][]byte{append([]byte(nil), data...)}
	}
	result := make([][]byte, 0, (len(data)+limit-1)/limit)
	for start := 0; start < len(data); start += limit {
		end := start + limit
		if end > len(data) {
			end = len(data)
		}
		result = append(result, append([]byte(nil), data[start:end]...))
	}
	return result
}

func normalizeIPv4(ip net.IP) net.IP {
	if ip == nil {
		return append(net.IP(nil), LocalStubIP...)
	}
	if ip4 := ip.To4(); ip4 != nil {
		return append(net.IP(nil), ip4...)
	}
	return append(net.IP(nil), LocalStubIP...)
}
