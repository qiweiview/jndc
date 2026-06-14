package secret

import (
	"crypto/aes"
	"crypto/cipher"
	"crypto/rand"
	"crypto/sha256"
	"fmt"

	"golang.org/x/crypto/pbkdf2"
)

const (
	gcmNonceLength   = 12
	pbkdf2Iterations = 65536
	keyLength        = 16
)

var salt = []byte("jndc-salt-v1")

type Cipher struct {
	key []byte
}

func New(secret string) *Cipher {
	key := pbkdf2.Key([]byte(secret), salt, pbkdf2Iterations, keyLength, sha256.New)
	return &Cipher{key: key}
}

func (c *Cipher) Encode(plaintext []byte) ([]byte, error) {
	nonce := make([]byte, gcmNonceLength)
	if _, err := rand.Read(nonce); err != nil {
		return nil, err
	}
	return c.EncodeWithNonce(plaintext, nonce)
}

func (c *Cipher) EncodeWithNonce(plaintext, nonce []byte) ([]byte, error) {
	if len(nonce) != gcmNonceLength {
		return nil, fmt.Errorf("unexpected nonce length: %d", len(nonce))
	}
	block, err := aes.NewCipher(c.key)
	if err != nil {
		return nil, err
	}
	gcm, err := cipher.NewGCM(block)
	if err != nil {
		return nil, err
	}
	ciphertext := gcm.Seal(nil, nonce, plaintext, nil)
	result := make([]byte, 0, len(nonce)+len(ciphertext))
	result = append(result, nonce...)
	result = append(result, ciphertext...)
	return result, nil
}

func (c *Cipher) Decode(encoded []byte) ([]byte, error) {
	if len(encoded) < gcmNonceLength {
		return nil, fmt.Errorf("ciphertext too short: %d", len(encoded))
	}
	nonce := encoded[:gcmNonceLength]
	ciphertext := encoded[gcmNonceLength:]
	block, err := aes.NewCipher(c.key)
	if err != nil {
		return nil, err
	}
	gcm, err := cipher.NewGCM(block)
	if err != nil {
		return nil, err
	}
	return gcm.Open(nil, nonce, ciphertext, nil)
}
