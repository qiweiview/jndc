package com.view.core.utils;



import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * aes工具
 */
public class AESUtils {

    private static String key = "02bda140-8875-4a";
    private static String iv = "14e34c6b-dbd9-45";

    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5PADDING";
    private static final String KEY_ALGORITHM = "AES";
    private static final String ENCODING = StandardCharsets.UTF_8.name();


    public static String encrypt(byte[] bytes) {
        return encrypt(new String(bytes, StandardCharsets.UTF_8));
    }


    public static String encryptWithoutSlant(String data) {
        //同时支持linux和windows环境
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            byte[] keyBytes = key.getBytes(ENCODING);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
            IvParameterSpec ivParams = new IvParameterSpec(iv.getBytes(ENCODING));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);
            byte[] encrypted = cipher.doFinal(data.getBytes(ENCODING));
            return Base64.getUrlEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt data", e);
        }
    }

    public static String decryptWithoutSlant(String encryptedData) {
        //同时支持linux和windows环境
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            byte[] keyBytes = key.getBytes(ENCODING);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
            IvParameterSpec ivParams = new IvParameterSpec(iv.getBytes(ENCODING));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);
            byte[] decoded = Base64.getUrlDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, ENCODING);
        } catch (Exception e) {
            throw new RuntimeException("解密失败");
        }
    }

    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            byte[] keyBytes = key.getBytes(ENCODING);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
            IvParameterSpec ivParams = new IvParameterSpec(iv.getBytes(ENCODING));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);
            byte[] encrypted = cipher.doFinal(data.getBytes(ENCODING));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt data", e);
        }
    }


    public static String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            byte[] keyBytes = key.getBytes(ENCODING);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
            IvParameterSpec ivParams = new IvParameterSpec(iv.getBytes(ENCODING));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, ENCODING);
        } catch (Exception e) {
            throw new RuntimeException("解密失败");
        }
    }
}
