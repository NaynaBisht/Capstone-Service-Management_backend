package com.app.management.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						
						// ADMIN & SERVICE_MANAGER can CREATE services
		                .requestMatchers(HttpMethod.POST, "/api/services")
		                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SERVICE_MANAGER")

		                // ADMIN & SERVICE_MANAGER can UPDATE services
		                .requestMatchers(HttpMethod.PUT, "/api/services/**")
		                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SERVICE_MANAGER")

						.requestMatchers(HttpMethod.GET, "/api/services").permitAll()
						
						.anyRequest().authenticated())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
