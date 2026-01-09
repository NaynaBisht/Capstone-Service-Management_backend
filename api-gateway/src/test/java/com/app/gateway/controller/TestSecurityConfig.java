package com.app.gateway.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
class TestSecurityConfig {

    @Bean
    SecurityWebFilterChain testSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/fallback").permitAll()   // ✅ allow fallback
                        .anyExchange().authenticated()
                )
                .build();
    }
}
