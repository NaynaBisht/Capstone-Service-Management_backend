package com.app.assignment.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

				.authorizeHttpRequests(auth -> auth

						// INTERNAL SERVICE-TO-SERVICE
						.requestMatchers("/internal/**").permitAll()

						// SERVICE MANAGER
						.requestMatchers(HttpMethod.POST, "/api/assignments").hasRole("SERVICE_MANAGER")

						.requestMatchers(HttpMethod.PUT, "/api/assignments/*/reassign").hasRole("SERVICE_MANAGER")

						.requestMatchers(HttpMethod.PUT, "/api/assignments/*/cancel").hasRole("SERVICE_MANAGER")

						// TECHNICIAN
						.requestMatchers(HttpMethod.GET, "/api/assignments/my").hasRole("TECHNICIAN")

						.requestMatchers(HttpMethod.PUT, "/api/assignments/*/accept").hasRole("TECHNICIAN")

						.requestMatchers(HttpMethod.PUT, "/api/assignments/*/reject").hasRole("TECHNICIAN")

						.requestMatchers(HttpMethod.PUT, "/api/assignments/*/start").hasRole("TECHNICIAN")

						.requestMatchers(HttpMethod.PUT, "/api/assignments/*/complete").hasRole("TECHNICIAN")

						.anyRequest().authenticated());

		return http.build();
	}

}
