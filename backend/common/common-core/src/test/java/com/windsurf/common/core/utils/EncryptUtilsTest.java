package com.windsurf.common.core.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for EncryptUtils
 */
class EncryptUtilsTest {

    private static final String TEST_STRING = "Hello, World!";
    private static final String TEST_KEY = "0123456789abcdef";

    @Test
    void testMd5() {
        String md5 = EncryptUtils.md5(TEST_STRING);
        assertNotNull(md5);
        assertEquals(32, md5.length());
        assertEquals(EncryptUtils.md5(TEST_STRING), md5); // Should be deterministic
    }

    @Test
    void testSha256() {
        String sha256 = EncryptUtils.sha256(TEST_STRING);
        assertNotNull(sha256);
        assertEquals(64, sha256.length());
        assertEquals(EncryptUtils.sha256(TEST_STRING), sha256); // Should be deterministic
    }

    @Test
    void testSha512() {
        String sha512 = EncryptUtils.sha512(TEST_STRING);
        assertNotNull(sha512);
        assertEquals(128, sha512.length());
        assertEquals(EncryptUtils.sha512(TEST_STRING), sha512); // Should be deterministic
    }

    @Test
    void testBase64Encode() {
        String encoded = EncryptUtils.base64Encode(TEST_STRING);
        assertNotNull(encoded);
        String decoded = EncryptUtils.base64Decode(encoded);
        assertEquals(TEST_STRING, decoded);
    }

    @Test
    void testBase64Decode() {
        String encoded = EncryptUtils.base64Encode(TEST_STRING);
        String decoded = EncryptUtils.base64Decode(encoded);
        assertNotNull(decoded);
        assertEquals(TEST_STRING, decoded);
    }

    @Test
    void testAesEncryptAndDecrypt() {
        String encrypted = EncryptUtils.aesEncrypt(TEST_STRING, TEST_KEY);
        assertNotNull(encrypted);
        String decrypted = EncryptUtils.aesDecrypt(encrypted, TEST_KEY);
        assertEquals(TEST_STRING, decrypted);
    }

    @Test
    void testAesEncryptWithInvalidKey() {
        assertThrows(RuntimeException.class, () -> {
            EncryptUtils.aesEncrypt(TEST_STRING, "short"); // Key too short
        });
    }

    @Test
    void testAesDecryptWithInvalidKey() {
        String encrypted = EncryptUtils.aesEncrypt(TEST_STRING, TEST_KEY);
        assertThrows(RuntimeException.class, () -> {
            EncryptUtils.aesDecrypt(encrypted, "wrongkey1234567890"); // Wrong key
        });
    }

    @Test
    void testHmacSha256() {
        String hmac = EncryptUtils.hmacSha256(TEST_STRING, TEST_KEY);
        assertNotNull(hmac);
        // Base64 encoded HMAC-SHA256 should be 44 characters
        assertEquals(44, hmac.length());
        assertEquals(EncryptUtils.hmacSha256(TEST_STRING, TEST_KEY), hmac); // Should be deterministic
    }

    @Test
    void testGenerateSalt() {
        String salt1 = EncryptUtils.generateSalt();
        String salt2 = EncryptUtils.generateSalt();
        assertNotNull(salt1);
        assertNotNull(salt2);
        assertNotEquals(salt1, salt2); // Should be random
    }

    @Test
    void testHashPassword() {
        String salt = EncryptUtils.generateSalt();
        String hash1 = EncryptUtils.hashPassword(TEST_STRING, salt);
        String hash2 = EncryptUtils.hashPassword(TEST_STRING, salt);
        assertNotNull(hash1);
        assertEquals(hash1, hash2); // Should be deterministic with same salt
    }

    @Test
    void testHashPasswordWithDifferentSalts() {
        String salt1 = EncryptUtils.generateSalt();
        String salt2 = EncryptUtils.generateSalt();
        String hash1 = EncryptUtils.hashPassword(TEST_STRING, salt1);
        String hash2 = EncryptUtils.hashPassword(TEST_STRING, salt2);
        assertNotEquals(hash1, hash2); // Should be different with different salts
    }

    @Test
    void testNullInputs() {
        assertThrows(IllegalArgumentException.class, () -> EncryptUtils.md5(null));
        assertThrows(IllegalArgumentException.class, () -> EncryptUtils.sha256(null));
        assertThrows(IllegalArgumentException.class, () -> EncryptUtils.sha512(null));
        assertThrows(IllegalArgumentException.class, () -> EncryptUtils.base64Encode(null));
        assertThrows(IllegalArgumentException.class, () -> EncryptUtils.base64Decode(null));
        assertThrows(IllegalArgumentException.class, () -> EncryptUtils.aesEncrypt(null, TEST_KEY));
        assertThrows(IllegalArgumentException.class, () -> EncryptUtils.aesEncrypt(TEST_STRING, null));
        assertThrows(IllegalArgumentException.class, () -> EncryptUtils.aesDecrypt(null, TEST_KEY));
        assertThrows(IllegalArgumentException.class, () -> EncryptUtils.aesDecrypt(TEST_STRING, null));
        assertThrows(IllegalArgumentException.class, () -> EncryptUtils.hmacSha256(null, TEST_KEY));
        assertThrows(IllegalArgumentException.class, () -> EncryptUtils.hmacSha256(TEST_STRING, null));
        assertThrows(IllegalArgumentException.class, () -> EncryptUtils.hashPassword(null, TEST_KEY));
        assertThrows(IllegalArgumentException.class, () -> EncryptUtils.hashPassword(TEST_STRING, null));
    }
}
