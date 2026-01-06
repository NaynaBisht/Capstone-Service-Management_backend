package com.app.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

	@RequestMapping("/fallback")
	public Mono<String> fallback() {
		return Mono.just("Service is taking too long to respond or is down. Please try again later.");
	}
}
