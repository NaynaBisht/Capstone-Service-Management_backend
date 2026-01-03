package com.app.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.app.gateway.filter.AuthenticationFilter;

@Configuration
public class GatewayConfig {

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder, AuthenticationFilter authFilter) {

		return builder.routes()

				// 1. AUTH SERVICE (Public + Circuit Breaker)
				.route("auth-service", r -> r.path("/api/auth/**").filters(
						f -> f.circuitBreaker(c -> c.setName("auth-service").setFallbackUri("forward:/fallback")))
						.uri("lb://auth-service"))

				// 2. BOOKING SERVICE (Secured + Circuit Breaker)
				// Note: We chain filters here: Auth Filter -> Circuit Breaker
				.route("booking-service",
						r -> r.path("/api/bookings/**")
								.filters(f -> f.filter(authFilter).circuitBreaker(
										c -> c.setName("booking-service").setFallbackUri("forward:/fallback")))
								.uri("lb://booking-service"))

				// 3. SERVICE MANAGEMENT (Secured + Circuit Breaker)
				.route("service-management-service",
						r -> r.path("/api/services/**").filters(f -> f.filter(authFilter).circuitBreaker(
								c -> c.setName("service-management-service").setFallbackUri("forward:/fallback")))
								.uri("lb://service-management-service"))

				// 4. ASSIGNMENT SERVICE (Secured + Circuit Breaker)
				.route("assignment-service",
						r -> r.path("/api/assignments/**")
								.filters(f -> f.filter(authFilter).circuitBreaker(
										c -> c.setName("assignment-service").setFallbackUri("forward:/fallback")))
								.uri("lb://assignment-service"))

				// 5. TECHNICIAN SERVICE (Secured + Circuit Breaker)
				// PUBLIC
				.route("technician-service-public",
						r -> r.path("/api/technicians/onboard", "/api/technicians/search", "/api/technicians/*/documents")
								.uri("lb://technician-service"))

				// SECURED
				.route("technician-service-secured",
						r -> r.path("/api/technicians/**")
								.filters(f -> f.filter(authFilter).circuitBreaker(
										c -> c.setName("technician-service").setFallbackUri("forward:/fallback")))
								.uri("lb://technician-service"))

				.build();
	}
}