package com.app.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.app.gateway.security.JwtUtil;
import com.app.gateway.security.RouteValidator;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GatewayFilter {

    private final JwtUtil jwtUtil;
    private final RouteValidator routeValidator;

    public AuthenticationFilter(JwtUtil jwtUtil,
                                RouteValidator routeValidator) {
        this.jwtUtil = jwtUtil;
        this.routeValidator = routeValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        if (routeValidator.isSecured.test(exchange.getRequest())) {

            if (!exchange.getRequest()
                    .getHeaders()
                    .containsKey(HttpHeaders.AUTHORIZATION)) {
                return Mono.error(
                        new RuntimeException("Missing Authorization header")
                );
            }

            String authHeader =
                    exchange.getRequest()
                            .getHeaders()
                            .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Mono.error(
                        new RuntimeException("Invalid Authorization header")
                );
            }

            String token = authHeader.substring(7);
            jwtUtil.validateToken(token);
        }

        return chain.filter(exchange);
    }
}
