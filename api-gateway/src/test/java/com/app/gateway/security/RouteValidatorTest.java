package com.app.gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RouteValidatorTest {

    private final RouteValidator validator = new RouteValidator();

    @Test
    void testIsSecured_WhenPathIsPublic_ShouldReturnFalse() {
        // Test login endpoint
        ServerHttpRequest request = MockServerHttpRequest.get("/auth/login").build();
        assertFalse(validator.isSecured.test(request), "Login should not be secured");

        // Test health endpoint
        ServerHttpRequest healthReq = MockServerHttpRequest.get("/actuator/health").build();
        assertFalse(validator.isSecured.test(healthReq), "Health check should not be secured");
    }

    @Test
    void testIsSecured_WhenPathIsPrivate_ShouldReturnTrue() {
        // Test some random secure API
        ServerHttpRequest request = MockServerHttpRequest.get("/api/v1/orders").build();
        assertTrue(validator.isSecured.test(request), "Orders API should be secured");
    }
}