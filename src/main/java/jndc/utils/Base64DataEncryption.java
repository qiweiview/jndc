package jndc.utils;

import java.util.Base64;

public class Base64DataEncryption implements DataEncryption{
    private static  Base64.Decoder decoder = Base64.getDecoder();
    private static  Base64.Encoder encoder = Base64.getEncoder();

    @Override
    public byte[] encode(byte[] bytes) {
        return  encoder.encode(bytes);
    }

    @Override
    public byte[] decode(byte[] bytes) {
        return  decoder.decode(bytes);
    }
}
