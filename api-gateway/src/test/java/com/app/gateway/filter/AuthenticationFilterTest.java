package com.app.gateway.filter;

import com.app.gateway.security.JwtUtil;
import com.app.gateway.security.RouteValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RouteValidator routeValidator;

    @Mock
    private GatewayFilterChain chain;

    private AuthenticationFilter authenticationFilter;

    @BeforeEach
    void setUp() {
        authenticationFilter = new AuthenticationFilter(jwtUtil, routeValidator);
        // We mock the Predicate here once so it's available for all tests
        routeValidator.isSecured = mock(Predicate.class);
    }

    @Test
    void testFilter_WhenRouteIsNotSecured_ShouldPass() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest.get("/public").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        
        when(routeValidator.isSecured.test(any())).thenReturn(false);
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(authenticationFilter.filter(exchange, chain))
                .verifyComplete();

        verify(chain).filter(exchange);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void testFilter_MissingAuthHeader_ShouldThrowException() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest.get("/secure").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(routeValidator.isSecured.test(any())).thenReturn(true);

        // Act & Assert
        StepVerifier.create(authenticationFilter.filter(exchange, chain))
                .expectErrorMessage("Missing Authorization header")
                .verify();
        
        // Ensure the chain was NEVER called because of the error
        verifyNoInteractions(chain);
    }

    @Test
    void testFilter_InvalidBearerPrefix_ShouldThrowException() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest.get("/secure")
                .header(HttpHeaders.AUTHORIZATION, "Basic 12345")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(routeValidator.isSecured.test(any())).thenReturn(true);

        // Act & Assert
        StepVerifier.create(authenticationFilter.filter(exchange, chain))
                .expectErrorMessage("Invalid Authorization header")
                .verify();
    }

    @Test
    void testFilter_ValidToken_ShouldPass() {
        // Arrange
        String token = "valid-token";
        MockServerHttpRequest request = MockServerHttpRequest.get("/secure")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(routeValidator.isSecured.test(any())).thenReturn(true);
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        doNothing().when(jwtUtil).validateToken(token);

        // Act & Assert
        StepVerifier.create(authenticationFilter.filter(exchange, chain))
                .verifyComplete();

        verify(jwtUtil).validateToken(token);
        verify(chain).filter(exchange);
    }
}