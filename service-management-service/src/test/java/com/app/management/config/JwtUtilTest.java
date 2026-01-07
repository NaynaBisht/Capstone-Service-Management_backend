package com.app.management.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String secret = "mySecretKeyForTestingPurposesOnlyMustBeLongEnough";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(secret);
    }

    @Test
    void shouldExtractAllClaims() {
        // Generate a token to test extraction
        String token = Jwts.builder()
                .setSubject("testUser")
                .claim("role", "USER")
                .setIssuedAt(new Date())
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        Claims claims = jwtUtil.extractAllClaims(token);

        assertEquals("testUser", claims.getSubject());
        assertEquals("USER", claims.get("role"));
    }

    @Test
    void shouldThrowExceptionForInvalidToken() {
        assertThrows(Exception.class, () -> {
            jwtUtil.extractAllClaims("invalid.token.here");
        });
    }
}