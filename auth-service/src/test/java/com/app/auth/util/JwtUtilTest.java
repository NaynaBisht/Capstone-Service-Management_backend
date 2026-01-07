package com.app.auth.util;

import com.app.auth.model.Role;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    
    // Secret must be at least 256 bits (32 characters) for HS256
    private final String secret = "mySecretKeyForTestingPurposesOnlyMustBeLongEnough";
    private final long expiration = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        // Manually injecting values usually handled by @Value
        jwtUtil = new JwtUtil(secret, expiration);
    }

    @Test
    void shouldGenerateAndExtractTokenSuccessfully() {
        // Arrange
        String userId = "user-123";
        Role role = Role.ADMIN;

        // Act: Test generateToken (exercises line 31-39)
        String token = jwtUtil.generateToken(userId, role);

        // Assert
        assertNotNull(token);

        // Act: Test extractAllClaims (exercises line 41-46)
        Claims claims = jwtUtil.extractAllClaims(token);

        // Assert
        assertEquals(userId, claims.getSubject());
        assertEquals("ADMIN", claims.get("role"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void shouldThrowExceptionForInvalidToken() {
        String invalidToken = "not.a.valid.jwt.token";
        
        // This exercises the failure path of the parser
        assertThrows(Exception.class, () -> {
            jwtUtil.extractAllClaims(invalidToken);
        });
    }
}