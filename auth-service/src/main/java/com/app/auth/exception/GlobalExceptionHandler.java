package com.app.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, Object> response = new HashMap<>();
        
        // 1. timestamp: Good for debugging logs later
        response.put("timestamp", Instant.now());
        
        // 2. status: The HTTP code (e.g., 409)
        response.put("status", ex.getStatusCode().value());
        
        // 3. custom message ("Email already in use")
        // We use getReason() since we passed the message as the second argument
        response.put("message", ex.getReason());

        // Note: We intentionally DO NOT add "trace" here. 
        // This ensures it is impossible for the stack trace to leak.

        return new ResponseEntity<>(response, ex.getStatusCode());
    }
    
    // Handle generic logical errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("message", "An unexpected error occurred"); 
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}