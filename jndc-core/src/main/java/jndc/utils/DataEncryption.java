package jndc.utils;

public interface DataEncryption {
    public byte[] encode(byte[] bytes);

    public byte[] decode(byte[] bytes);
}
