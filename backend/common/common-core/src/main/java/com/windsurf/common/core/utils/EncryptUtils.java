package com.windsurf.common.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Encryption utility class
 */
@Slf4j
public class EncryptUtils {

    private static final String AES_ALGORITHM = "AES";
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    private static final int AES_KEY_SIZE = 256;
    private static final int AES_KEY_LENGTH = 16; // 128 bits

    /**
     * MD5 encryption
     */
    public static String md5(String data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Input data cannot be null or empty");
        }
        return DigestUtils.md5Hex(data);
    }

    /**
     * SHA-256 encryption
     */
    public static String sha256(String data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Input data cannot be null or empty");
        }
        return DigestUtils.sha256Hex(data);
    }

    /**
     * SHA-512 encryption
     */
    public static String sha512(String data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Input data cannot be null or empty");
        }
        return DigestUtils.sha512Hex(data);
    }

    /**
     * Base64 encoding
     */
    public static String base64Encode(String data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Input data cannot be null or empty");
        }
        return Base64.encodeBase64String(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64 decoding
     */
    public static String base64Decode(String data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Input data cannot be null or empty");
        }
        return new String(Base64.decodeBase64(data), StandardCharsets.UTF_8);
    }

    /**
     * Generate AES key
     */
    public static SecretKey generateAesKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
        SecureRandom secureRandom = new SecureRandom();
        keyGen.init(AES_KEY_SIZE, secureRandom);
        return keyGen.generateKey();
    }

    /**
     * AES encryption
     */
    public static String aesEncrypt(String data, String key) throws IllegalArgumentException {
        if (data == null || data.isEmpty() || key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Input data and key cannot be null or empty");
        }

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length != AES_KEY_LENGTH) {
            throw new IllegalArgumentException("AES key must be 16 bytes (128 bits) long");
        }

        try {
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.encodeBase64String(encryptedBytes);
        } catch (Exception e) {
            log.error("AES encryption failed", e);
            throw new RuntimeException("AES encryption failed", e);
        }
    }

    /**
     * AES decryption
     */
    public static String aesDecrypt(String encryptedData, String key) {
        if (encryptedData == null || encryptedData.isEmpty() || key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Input encrypted data and key cannot be null or empty");
        }

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length != AES_KEY_LENGTH) {
            throw new IllegalArgumentException("AES key must be 16 bytes (128 bits) long");
        }

        try {
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decryptedBytes = cipher.doFinal(Base64.decodeBase64(encryptedData));
            return new String(decryptedBytes);
        } catch (Exception e) {
            log.error("AES decryption failed", e);
            throw new RuntimeException("AES decryption failed", e);
        }
    }

    /**
     * Generate HMAC-SHA256
     */
    public static String hmacSha256(String data, String key) {
        if (data == null || data.isEmpty() || key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Input data and key cannot be null or empty");
        }

        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA256_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(secretKeySpec);

            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(hmacBytes);
        } catch (Exception e) {
            log.error("HMAC-SHA256 generation failed", e);
            throw new RuntimeException("HMAC-SHA256 generation failed", e);
        }
    }

    /**
     * Generate random salt
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.encodeBase64String(salt);
    }

    /**
     * Hash password with salt
     */
    public static String hashPassword(String password, String salt) {
        if (password == null || password.isEmpty() || salt == null || salt.isEmpty()) {
            throw new IllegalArgumentException("Input password and salt cannot be null or empty");
        }
        return sha256(password + salt);
    }
}