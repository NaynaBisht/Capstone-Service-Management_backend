package com.app.booking.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    // Must be at least 256 bits for HS256
    private final String secret = "super-secret-key-that-is-long-enough-for-hs256";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(secret);
    }

    @Test
    void shouldExtractUserIdAndRoleFromToken() {
        String token = Jwts.builder()
                .setSubject("booking-user")
                .claim("role", "CUSTOMER")
                .setIssuedAt(new Date())
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        assertEquals("booking-user", jwtUtil.extractUserId(token));
        assertEquals("CUSTOMER", jwtUtil.extractRole(token));
    }

    @Test
    void shouldThrowExceptionForInvalidToken() {
        String invalidToken = "not.a.real.token";
        assertThrows(Exception.class, () -> jwtUtil.extractUserId(invalidToken));
    }
}