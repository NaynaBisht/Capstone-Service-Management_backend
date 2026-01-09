package com.app.assignment.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateAssignmentRequestTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidRequest() {
        CreateAssignmentRequest request = new CreateAssignmentRequest();
        request.setBookingId("B1");
        request.setCategoryId("C1");
        request.setTechnicianId("T1");
        request.setScheduledDate(LocalDate.now());
        request.setTimeSlot("10AM-12PM");

        Set<ConstraintViolation<CreateAssignmentRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidRequest_missingFields() {
        CreateAssignmentRequest request = new CreateAssignmentRequest();

        Set<ConstraintViolation<CreateAssignmentRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void testScheduledDateInPast_invalid() {
        CreateAssignmentRequest request = new CreateAssignmentRequest();
        request.setBookingId("B1");
        request.setCategoryId("C1");
        request.setScheduledDate(LocalDate.now().minusDays(1));
        request.setTimeSlot("10AM-12PM");

        Set<ConstraintViolation<CreateAssignmentRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void testGettersAndSetters() {
        CreateAssignmentRequest request = new CreateAssignmentRequest();
        request.setBookingId("B1");
        request.setCategoryId("C1");
        request.setTechnicianId("T1");
        request.setScheduledDate(LocalDate.now());
        request.setTimeSlot("9AM-11AM");

        assertEquals("B1", request.getBookingId());
        assertEquals("C1", request.getCategoryId());
        assertEquals("T1", request.getTechnicianId());
        assertEquals("9AM-11AM", request.getTimeSlot());
    }
}
