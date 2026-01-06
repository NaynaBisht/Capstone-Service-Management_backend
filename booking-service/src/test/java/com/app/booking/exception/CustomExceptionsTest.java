package com.app.booking.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CustomExceptionsTest {

    @Test
    void testBusinessValidationException() {
        String msg = "Too late to cancel";
        BusinessValidationException ex = new BusinessValidationException(msg);

        assertEquals(msg, ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
    }

    @Test
    void testBookingNotFoundException() {
        String id = "BK-123";
        BookingNotFoundException ex = new BookingNotFoundException(id);

        // Verify the string concatenation logic inside the constructor
        assertEquals("Booking not found for id: BK-123", ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
    }
}