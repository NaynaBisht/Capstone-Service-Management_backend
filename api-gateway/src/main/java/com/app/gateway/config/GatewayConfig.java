package com.app.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.app.gateway.filter.AuthenticationFilter;

@Configuration
public class GatewayConfig {

	private static final String FALLBACK_URI = "forward:/fallback";

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder, AuthenticationFilter authFilter) {

		return builder.routes()

				// 1. AUTH SERVICE (Public + Circuit Breaker)
				.route("auth-service-public",
						r -> r.path("/api/auth/login", "/api/auth/register").uri("lb://auth-service"))

				.route("auth-service-secured",
						r -> r.path("/api/auth/me", "/api/auth/create-manager").filters(f -> f.filter(authFilter))
								.uri("lb://auth-service"))

				// 2. BOOKING SERVICE (Secured + Circuit Breaker)
				// Note: We chain filters here: Auth Filter -> Circuit Breaker
				.route("booking-service",
						r -> r.path("/api/bookings/**")
								.filters(f -> f.filter(authFilter)
										.circuitBreaker(c -> c.setName("booking-service").setFallbackUri(FALLBACK_URI)))
								.uri("lb://booking-service"))

				// 3. SERVICE MANAGEMENT (Secured + Circuit Breaker)
				.route("service-management-service",
						r -> r.path("/api/services/**")
								.filters(f -> f.filter(authFilter).circuitBreaker(
										c -> c.setName("service-management-service").setFallbackUri(FALLBACK_URI)))
								.uri("lb://service-management-service"))

				// 4. ASSIGNMENT SERVICE (Secured + Circuit Breaker)
				.route("assignment-service",
						r -> r.path("/api/assignments/**")
								.filters(f -> f.filter(authFilter).circuitBreaker(
										c -> c.setName("assignment-service").setFallbackUri(FALLBACK_URI)))
								.uri("lb://assignment-service"))

				// 5. TECHNICIAN SERVICE (Secured + Circuit Breaker)
				// PUBLIC
				.route("technician-service-public",
						r -> r.path("/api/technicians/onboard", "/api/technicians/search",
								"/api/technicians/*/documents").uri("lb://technician-service"))

				// SECURED
				.route("technician-service-secured",
						r -> r.path("/api/technicians/**")
								.filters(f -> f.filter(authFilter).circuitBreaker(
										c -> c.setName("technician-service").setFallbackUri(FALLBACK_URI)))
								.uri("lb://technician-service"))

				.build();
	}
}