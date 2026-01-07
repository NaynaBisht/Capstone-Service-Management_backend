package com.app.technician.exception;

import com.app.technician.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test-path");
    }

    @Test
    void handleValidationExceptions_ShouldReturnBadRequest() {
        // 1. Mocking the complex MethodArgumentNotValidException
        MethodArgumentNotValidException ex = Mockito.mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "email", "must be valid");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleValidationExceptions(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getValidationErrors());
        assertEquals("must be valid", response.getBody().getValidationErrors().get("email"));
    }

    @Test
    void handleIllegalState_ShouldReturnConflict() {
        IllegalStateException ex = new IllegalStateException("Conflict message");
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleIllegalState(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Conflict message", response.getBody().getMessage());
    }

    @Test
    void handleIllegalArgument_ShouldReturnBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Bad argument");
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleIllegalArgument(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad argument", response.getBody().getMessage());
    }

    @Test
    void handleRuntimeException_WithNotFound_ShouldReturn404() {
        // This covers the specific "if" branch in your handleRuntimeException
        RuntimeException ex = new RuntimeException("User not found");
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleRuntimeException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("not found"));
    }

    @Test
    void handleRuntimeException_Generic_ShouldReturn500() {
        // This covers the "else" branch (the generic error)
        RuntimeException ex = new RuntimeException("Something went wrong");
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleRuntimeException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }
}