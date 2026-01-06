package com.app.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		return http
				// 1. Disable CSRF (The source of your 403 error)
				.csrf(ServerHttpSecurity.CsrfSpec::disable)

				// 2. Allow everything here.
				// Why? Because you are already handling specific route security
				// in your 'GatewayConfig.java' using your custom 'AuthenticationFilter'.
				.authorizeExchange(exchange -> exchange.anyExchange().permitAll()).build();
	}
}