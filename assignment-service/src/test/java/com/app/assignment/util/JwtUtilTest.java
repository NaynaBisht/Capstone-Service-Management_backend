package com.app.assignment.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String secret = "myVerySecretKeyThatIsAtLeast32BytesLong!!";
    private String validToken;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Inject the secret manually since @Value won't work in a plain JUnit test
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);

        // Generate a valid token for testing
        validToken = Jwts.builder()
                .setSubject("user-123")
                .claim("role", "CUSTOMER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void extractAllClaims_success() {
        Claims claims = jwtUtil.extractAllClaims(validToken);
        
        assertNotNull(claims);
        assertEquals("user-123", claims.getSubject());
        assertEquals("CUSTOMER", claims.get("role"));
    }

    @Test
    void extractUserId_success() {
        String userId = jwtUtil.extractUserId(validToken);
        assertEquals("user-123", userId);
    }

    @Test
    void extractRole_success() {
        String role = jwtUtil.extractRole(validToken);
        assertEquals("CUSTOMER", role);
    }

    @Test
    void extractAllClaims_invalidToken_throwsException() {
        String invalidToken = "this.is.not.a.valid.token";
        
        assertThrows(Exception.class, () -> {
            jwtUtil.extractAllClaims(invalidToken);
        });
    }
}