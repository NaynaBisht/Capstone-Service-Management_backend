package com.app.booking.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import com.app.booking.dto.error.ErrorResponse;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
    }

    @Test
    void testHandleValidationExceptions() {
        // Arrange: Mock the complex validation exception hierarchy
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        // Create a mock FieldError
        FieldError fieldError = new FieldError("objectName", "email", "must be valid");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(ex, webRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation Failed", response.getBody().getError());
        assertEquals("must be valid", response.getBody().getValidationErrors().get("email"));
    }

    @Test
    void testHandleValidationExceptions_NoErrors() {
        // Edge case: Exception triggers but list is empty (defensive coding)
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Collections.emptyList());

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, response.getBody().getValidationErrors().size());
    }

    @Test
    void testHandleNoSuchElementException() {
        NoSuchElementException ex = new NoSuchElementException("Item missing");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNoSuchElementException(ex, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Resource Not Found", response.getBody().getError());
        assertEquals("Item missing", response.getBody().getMessage());
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid arg");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Invalid arg", response.getBody().getMessage());
    }

    @Test
    void testHandleBusinessValidation() {
        BusinessValidationException ex = new BusinessValidationException("Rule violated");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBusinessValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Business Rule Violation", response.getBody().getError());
        assertEquals("Rule violated", response.getBody().getMessage());
    }

    @Test
    void testHandleBookingNotFound() {
        BookingNotFoundException ex = new BookingNotFoundException("BK-999");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBookingNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Booking Not Found", response.getBody().getError());
        // Verify the formatting logic inside the exception
        assertEquals("Booking not found for id: BK-999", response.getBody().getMessage());
    }

    @Test
    void testHandleGlobalException() {
        RuntimeException ex = new RuntimeException("Unexpected crash");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody().getError());
        // Ensure we don't leak internal error details to the client
        assertEquals("Something went wrong. Please try again later.", response.getBody().getMessage());
    }
}