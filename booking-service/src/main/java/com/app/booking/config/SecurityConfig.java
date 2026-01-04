package com.app.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

            		.requestMatchers("/internal/**").permitAll()
                // CUSTOMER
                .requestMatchers(HttpMethod.POST, "/api/bookings").hasRole("CUSTOMER")
                .requestMatchers("/api/bookings/my-bookings").hasRole("CUSTOMER")
                .requestMatchers("/api/bookings/*/cancel").hasRole("CUSTOMER")
                .requestMatchers("/api/bookings/*/reschedule").hasRole("CUSTOMER")

                // ADMIN
                .requestMatchers("/api/bookings").hasRole("ADMIN")
                .requestMatchers("/api/bookings/history").hasRole("ADMIN")

                // TECHNICIAN
                .requestMatchers("/api/bookings/technician/**").hasRole("TECHNICIAN")

                .anyRequest().authenticated()
            )
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}

