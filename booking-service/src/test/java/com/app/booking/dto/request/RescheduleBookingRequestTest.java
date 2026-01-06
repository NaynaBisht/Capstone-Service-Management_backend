package com.app.booking.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.app.booking.model.TimeSlot;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class RescheduleBookingRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidRescheduleRequest() {
        RescheduleBookingRequest request = RescheduleBookingRequest.builder()
                .scheduledDate(LocalDate.now().plusDays(2))
                .timeSlot(TimeSlot.SLOT_14_16)
                .build();

        Set<ConstraintViolation<RescheduleBookingRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_NullFields() {
        RescheduleBookingRequest request = new RescheduleBookingRequest();

        Set<ConstraintViolation<RescheduleBookingRequest>> violations = validator.validate(request);
        
        // Expects 2 violations: scheduledDate and timeSlot
        assertEquals(2, violations.size());
    }

    @Test
    void testValidation_PastDate() {
        RescheduleBookingRequest request = RescheduleBookingRequest.builder()
                .scheduledDate(LocalDate.now().minusDays(1)) // Past
                .timeSlot(TimeSlot.SLOT_14_16)
                .build();

        Set<ConstraintViolation<RescheduleBookingRequest>> violations = validator.validate(request);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("Scheduled date cannot be in the past")));
    }

    @Test
    void testLombokMethods() {
        // 1. Constructors & Setters
        RescheduleBookingRequest req1 = new RescheduleBookingRequest();
        LocalDate now = LocalDate.now();
        req1.setScheduledDate(now);
        req1.setTimeSlot(TimeSlot.SLOT_14_16);

        // 2. Getters
        assertEquals(now, req1.getScheduledDate());
        assertEquals(TimeSlot.SLOT_14_16, req1.getTimeSlot());

        // 3. Equals & HashCode
        RescheduleBookingRequest req2 = RescheduleBookingRequest.builder()
                .scheduledDate(now)
                .timeSlot(TimeSlot.SLOT_14_16)
                .build();

        assertEquals(req1, req2);
        assertEquals(req1.hashCode(), req2.hashCode());

        req2.setTimeSlot(TimeSlot.SLOT_11_13);
        assertNotEquals(req1, req2);

        // 4. ToString
        assertTrue(req1.toString().contains("RescheduleBookingRequest"));
    }
}