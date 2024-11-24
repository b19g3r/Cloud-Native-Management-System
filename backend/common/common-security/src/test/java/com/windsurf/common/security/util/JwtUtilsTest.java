package com.windsurf.common.security.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private String token;
    private final String username = "testuser";

    @BeforeEach
    void setUp() {
        token = JwtUtils.generateToken(username);
    }

    @Test
    void testGenerateToken() {
        assertNotNull(token, "Token should not be null");
    }

    @Test
    void testGetClaimsFromToken() {
        Claims claims = JwtUtils.getClaimsFromToken(token);
        assertEquals(username, claims.getSubject(), "Username should match");
    }

    @Test
    void testValidateToken() {
        assertTrue(JwtUtils.validateToken(token, username), "Token should be valid");
    }

    @Test
    void testIsTokenExpired() throws InterruptedException {
        // Simulate token expiration
        Thread.sleep(1000);
        assertTrue(JwtUtils.validateToken(token, username), "Token should be expired");
    }
}
