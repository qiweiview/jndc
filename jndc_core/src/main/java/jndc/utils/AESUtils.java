package jndc.utils;


import jndc.exception.SecreteDecodeFailException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;


/**
 * AES-GCM 认证加密工具
 *
 * 密文格式: [12字节 nonce] + [GCM 密文 + 16字节 auth tag]
 */
@Slf4j
public class AESUtils {

    private static final int GCM_NONCE_LENGTH = 12;  // bytes
    private static final int GCM_TAG_LENGTH = 128;    // bits
    private static final int PBKDF2_ITERATIONS = 65536;
    private static final int KEY_LENGTH = 128;         // bits

    private static final String DEFAULT_KEY = "hi,view";

    private static volatile SecretKeySpec secretKey;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    static {
        setKey(DEFAULT_KEY.getBytes());
    }

    /**
     * 使用 PBKDF2 从密码派生 AES 密钥
     */
    public static void setKey(byte[] key) {
        try {
            // 使用固定 salt（协议双方需一致），生产环境建议每用户独立 salt
            byte[] salt = "jndc-salt-v1".getBytes();
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(new String(key).toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH);
            byte[] derivedKey = factory.generateSecret(spec).getEncoded();
            secretKey = new SecretKeySpec(derivedKey, "AES");
        } catch (Exception e) {
            log.error("设置密钥异常: " + e);
            throw new RuntimeException("set key error: " + e);
        }
    }

    /**
     * AES-GCM 加密
     *
     * @param bytes 明文
     * @return [12字节 nonce] + [密文 + auth tag]
     */
    public static byte[] encode(byte[] bytes) {
        try {
            // 生成随机 nonce
            byte[] nonce = new byte[GCM_NONCE_LENGTH];
            SECURE_RANDOM.nextBytes(nonce);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, nonce);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

            byte[] ciphertext = cipher.doFinal(bytes);

            // 拼接: nonce + ciphertext
            byte[] result = new byte[nonce.length + ciphertext.length];
            System.arraycopy(nonce, 0, result, 0, nonce.length);
            System.arraycopy(ciphertext, 0, result, nonce.length, ciphertext.length);
            return result;
        } catch (Exception e) {
            log.error("编码异常: " + e);
            throw new RuntimeException("encode error: " + e);
        }
    }

    /**
     * AES-GCM 解密
     *
     * @param bytes [12字节 nonce] + [密文 + auth tag]
     * @return 明文
     */
    public static byte[] decode(byte[] bytes) {
        try {
            if (bytes.length < GCM_NONCE_LENGTH) {
                throw new SecreteDecodeFailException();
            }

            // 提取 nonce
            byte[] nonce = new byte[GCM_NONCE_LENGTH];
            System.arraycopy(bytes, 0, nonce, 0, GCM_NONCE_LENGTH);

            // 提取密文
            byte[] ciphertext = new byte[bytes.length - GCM_NONCE_LENGTH];
            System.arraycopy(bytes, GCM_NONCE_LENGTH, ciphertext, 0, ciphertext.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, nonce);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

            return cipher.doFinal(ciphertext);
        } catch (SecreteDecodeFailException e) {
            throw e;
        } catch (Exception e) {
            log.error("解码异常: " + e + "，即将断开链接...");
            throw new SecreteDecodeFailException();
        }
    }
}
