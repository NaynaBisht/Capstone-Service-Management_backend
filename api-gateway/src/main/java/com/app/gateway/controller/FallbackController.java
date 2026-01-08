package com.app.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

	@RequestMapping("/fallback")
    public Mono<ResponseEntity<String>> fallback() {
        return Mono.just(
            ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Service is taking too long to respond or is down. Please try again later.")
        );
    }
}
