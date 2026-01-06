package com.app.technician.exception;

import com.app.technician.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle @Valid validation errors (400 Bad Request)
     * Example: @NotBlank, @Email violations in TechnicianOnboardRequest
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Failed", errors, request);
    }

    /**
     * Handle IllegalStateException (409 Conflict)
     * Used in your code for State transitions (e.g., "Only PENDING technicians can be approved")
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalState(
            IllegalStateException ex, HttpServletRequest request) {
        
        log.warn("State conflict: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), null, request);
    }

    /**
     * Handle IllegalArgumentException (400 Bad Request)
     * Used in your code for: "Technician already registered", "Technician not found"
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        
        log.warn("Illegal argument: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null, request);
    }

    /**
     * Handle generic RuntimeException (500 Internal Server Error)
     * Catches "No available technician found" from InternalController
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        
        // Special handling for the "Not found" runtime exception in InternalController
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")) {
            log.warn("Resource not found: {}", ex.getMessage());
            return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null, request);
        }

        log.error("Unexpected runtime error", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", null, request);
    }

    /**
     * Helper method to build the standardized response
     */
    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status, 
            String message, 
            Map<String, String> validationErrors, 
            HttpServletRequest request) {
        
        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .validationErrors(validationErrors)
                .build();

        return new ResponseEntity<>(response, status);
    }
}