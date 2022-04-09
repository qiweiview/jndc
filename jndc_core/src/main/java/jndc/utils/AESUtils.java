package jndc.utils;


import jndc.exception.SecreteDecodeFailException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;


/**
 * AESUtils
 */
@Slf4j
public class AESUtils {

    private static final byte[] DEFAULT_KEY = "hi,view".getBytes();

    private static SecretKeySpec secretKey;


    static {
        setKey(DEFAULT_KEY);
    }

    public static void setKey(byte[] key) {
        MessageDigest sha;
        try {
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            log.info("设置密钥异常" + e);
            throw new RuntimeException("set key error:" + e);
        }
    }

    public static byte[] encode(byte[] bytes) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            log.info("编码异常" + e);
            throw new RuntimeException("encode error:" + e);
        }

    }

    public static byte[] decode(byte[] bytes) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            log.info("解码异常" + e);
            throw new SecreteDecodeFailException();
        }
    }
}
