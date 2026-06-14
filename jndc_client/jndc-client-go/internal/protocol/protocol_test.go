package protocol

import (
	"bytes"
	"encoding/binary"
	"net"
	"testing"
)

func TestWriteAndReadMessageRoundTrip(t *testing.T) {
	t.Parallel()

	message := NewMessage(net.IPv4(1, 2, 3, 4), net.IPv4(10, 0, 0, 2), 18080, 1080, 8080, TCPData)
	message.Data = []byte("hello-jndc")

	var buffer bytes.Buffer
	if err := WriteMessage(&buffer, message); err != nil {
		t.Fatalf("write message: %v", err)
	}

	decoded, err := ReadMessage(&buffer)
	if err != nil {
		t.Fatalf("read message: %v", err)
	}

	if decoded.Type != message.Type {
		t.Fatalf("type mismatch: got %d want %d", decoded.Type, message.Type)
	}
	if decoded.LocalPort != message.LocalPort || decoded.ServerPort != message.ServerPort || decoded.RemotePort != message.RemotePort {
		t.Fatalf("port mismatch: got %+v want %+v", decoded, message)
	}
	if !decoded.LocalIP.Equal(message.LocalIP) || !decoded.RemoteIP.Equal(message.RemoteIP) {
		t.Fatalf("ip mismatch: got %v/%v want %v/%v", decoded.LocalIP, decoded.RemoteIP, message.LocalIP, message.RemoteIP)
	}
	if !bytes.Equal(decoded.Data, message.Data) {
		t.Fatalf("data mismatch: got %q want %q", decoded.Data, message.Data)
	}
}

func TestEncodeAutoSplitsLargePayload(t *testing.T) {
	t.Parallel()

	message := NewMessage(net.IPv4(1, 1, 1, 1), net.IPv4(2, 2, 2, 2), 1, 2, 3, TCPData)
	message.Data = bytes.Repeat([]byte("a"), AutoUnpackLength+17)

	frames, err := message.Encode()
	if err != nil {
		t.Fatalf("encode: %v", err)
	}
	if len(frames) != 2 {
		t.Fatalf("unexpected frame count: %d", len(frames))
	}

	first, size, err := ParseFixedHeader(frames[0][:FixLength])
	if err != nil {
		t.Fatalf("parse first frame: %v", err)
	}
	if size != AutoUnpackLength {
		t.Fatalf("unexpected first frame size: %d", size)
	}
	if first.Type != TCPData {
		t.Fatalf("unexpected first frame type: %d", first.Type)
	}
}

func TestParseFixedHeaderRejectsOversizedPayload(t *testing.T) {
	t.Parallel()

	header := make([]byte, FixLength)
	copy(header[:3], Magic)
	header[3] = 1
	header[4] = TCPData
	binary.BigEndian.PutUint32(header[25:29], uint32(AutoUnpackLength+1))

	if _, _, err := ParseFixedHeader(header); err == nil {
		t.Fatal("expected oversized payload to fail")
	}
}
