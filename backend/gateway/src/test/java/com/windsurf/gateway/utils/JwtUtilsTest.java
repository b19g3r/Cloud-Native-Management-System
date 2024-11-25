package com.windsurf.gateway.utils;

import com.windsurf.gateway.config.SecurityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @Mock
    private SecurityProperties securityProperties;

    @Mock
    private SecurityProperties.JwtProperties jwtProperties;

    private JwtUtils jwtUtils;
    private static final String SECRET = "testSecretKeyWithLength32BytesLong!";
    private static final String TEST_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0VXNlciIsImlhdCI6MTYzMDAwMDAwMCwiZXhwIjoxNjMwMDg2NDAwfQ.8Ub3hBnPC0HBVeqtL8oLS8mwcH5YJQl0q3OCH0Yhxis";
    private static final String TEST_USER_ID = "testUser";

    @BeforeEach
    void setUp() {
        when(securityProperties.getJwt()).thenReturn(jwtProperties);
        when(jwtProperties.getSecret()).thenReturn(SECRET);
        when(jwtProperties.getExpiration()).thenReturn(86400);
        jwtUtils = new JwtUtils(securityProperties);
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String token = jwtUtils.generateToken(TEST_USER_ID);
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        assertFalse(jwtUtils.validateToken("invalid.token.here"));
    }

    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0VXNlciIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxNTE2MjM5MDIyfQ.vqgZPF0oQYRHxzAG1aSd4SJ5vWbcW5YvLcBGZB4t5_A";
        assertFalse(jwtUtils.validateToken(expiredToken));
    }

    @Test
    void getUserIdFromToken_ValidToken_ReturnsUserId() {
        String token = jwtUtils.generateToken(TEST_USER_ID);
        assertEquals(TEST_USER_ID, jwtUtils.getUserIdFromToken(token));
    }

    @Test
    void getUserIdFromToken_InvalidToken_ThrowsException() {
        assertThrows(RuntimeException.class, () -> jwtUtils.getUserIdFromToken("invalid.token.here"));
    }

    @Test
    void generateToken_ValidUserId_ReturnsValidToken() {
        String token = jwtUtils.generateToken(TEST_USER_ID);
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // Header.Payload.Signature
        assertEquals(TEST_USER_ID, jwtUtils.getUserIdFromToken(token));
    }
}
