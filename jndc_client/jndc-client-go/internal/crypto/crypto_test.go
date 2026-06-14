package secret

import (
	"bytes"
	"encoding/hex"
	"testing"
)

func TestEncodeWithFixedNonceMatchesVector(t *testing.T) {
	t.Parallel()

	cipher := New("your-secret-key-here")
	nonce := []byte{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}

	encoded, err := cipher.EncodeWithNonce([]byte("hello-jndc"), nonce)
	if err != nil {
		t.Fatalf("encode: %v", err)
	}

	expected := "000102030405060708090a0b71f86e2431b0141f45a089a241c0a463b09a13bbc022d350bdfa"
	if hex.EncodeToString(encoded) != expected {
		t.Fatalf("vector mismatch: got %s want %s", hex.EncodeToString(encoded), expected)
	}

	decoded, err := cipher.Decode(encoded)
	if err != nil {
		t.Fatalf("decode: %v", err)
	}
	if !bytes.Equal(decoded, []byte("hello-jndc")) {
		t.Fatalf("decoded mismatch: got %q", decoded)
	}
}
