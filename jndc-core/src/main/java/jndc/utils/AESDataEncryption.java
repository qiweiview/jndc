package jndc.utils;


public class AESDataEncryption implements DataEncryption{


    @Override
    public byte[] encode(byte[] bytes) {
        return  AESUtils.encode(bytes);
    }

    @Override
    public byte[] decode(byte[] bytes) {
        return  AESUtils.decode(bytes);
    }
}
