package com.app.gateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String secret = "mySecretKeyForTestingPurposesMustBeLongEnough123456";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Manually inject the @Value field
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
    }

    @Test
    void testValidateToken_WithValidToken_ShouldNotThrowException() {
        // Create a valid token
        String token = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60)) // 1 min expiry
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        assertDoesNotThrow(() -> jwtUtil.validateToken(token));
    }

    @Test
    void testValidateToken_WithInvalidToken_ShouldThrowException() {
        String invalidToken = "this.is.not.a.valid.token";
        
        assertThrows(Exception.class, () -> jwtUtil.validateToken(invalidToken));
    }

    @Test
    void testValidateToken_WithExpiredToken_ShouldThrowException() {
        // Create an expired token
        String expiredToken = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        assertThrows(Exception.class, () -> jwtUtil.validateToken(expiredToken));
    }
}