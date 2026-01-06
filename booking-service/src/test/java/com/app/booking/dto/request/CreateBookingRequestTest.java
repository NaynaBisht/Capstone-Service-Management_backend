package com.app.booking.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.app.booking.model.Address;
import com.app.booking.model.PaymentMode;
import com.app.booking.model.TimeSlot;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class CreateBookingRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidCreateBookingRequest() {
        // Arrange
        CreateBookingRequest request = CreateBookingRequest.builder()
                .serviceId("srv-001")
                .serviceName("AC Repair")
                .categoryId("cat-001")
                .categoryName("Home Appliances")
                .scheduledDate(LocalDate.now().plusDays(1)) // Future date
                .timeSlot(TimeSlot.SLOT_11_13) // Assuming Enum
                .address(new Address()) // Assuming Empty constructor exists
                .issueDescription("Not cooling")
                .paymentMode(PaymentMode.CASH)
                .build();

        // Act
        Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty(), "Valid request should not have violations");
    }

    @Test
    void testValidation_NullFields() {
        // Arrange: Create object with missing required fields
        CreateBookingRequest request = new CreateBookingRequest(); 

        // Act
        Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        // We expect violations for: serviceId, serviceName, categoryId, categoryName, scheduledDate, timeSlot, address, issueDescription, paymentMode
        // Just checking size > 0 is usually enough, but checking strictly:
        assertTrue(violations.size() >= 9, "Should fail on all @NotNull/@NotBlank fields");
    }

    @Test
    void testValidation_PastDate() {
        // Arrange
        CreateBookingRequest request = CreateBookingRequest.builder()
                .serviceId("srv-001")
                .serviceName("AC Repair")
                .categoryId("cat-001")
                .categoryName("Home Appliances")
                .scheduledDate(LocalDate.now().minusDays(1)) // FAIL: Past Date
                .timeSlot(TimeSlot.SLOT_11_13)
                .address(new Address())
                .issueDescription("Issue")
                .paymentMode(PaymentMode.CASH)
                .build();

        // Act
        Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        boolean hasDateViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("scheduledDate"));
        assertTrue(hasDateViolation, "Should have violation for past scheduledDate");
    }

    @Test
    void testLombokMethods() {
        // This test ensures 100% coverage on generated getters, setters, equals, hashcode, toString
        
        // 1. Test NoArgs Constructor & Setters
        CreateBookingRequest request1 = new CreateBookingRequest();
        request1.setServiceId("S1");
        request1.setServiceName("Name");
        request1.setCategoryId("C1");
        request1.setCategoryName("CatName");
        request1.setScheduledDate(LocalDate.now());
        request1.setTimeSlot(TimeSlot.SLOT_11_13);
        request1.setAddress(new Address());
        request1.setIssueDescription("Desc");
        request1.setPaymentMode(PaymentMode.PREPAID);

        // 2. Test Getters
        assertEquals("S1", request1.getServiceId());
        assertEquals("Name", request1.getServiceName());
        assertEquals("C1", request1.getCategoryId());
        assertEquals(PaymentMode.PREPAID, request1.getPaymentMode());

        // 3. Test Equals & HashCode
        CreateBookingRequest request2 = new CreateBookingRequest(
                "S1", "Name", "C1", "CatName", 
                request1.getScheduledDate(), TimeSlot.SLOT_11_13, 
                request1.getAddress(), "Desc", PaymentMode.PREPAID);

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        
        request2.setServiceId("DIFFERENT");
        assertNotEquals(request1, request2);

        // 4. Test ToString
        String stringResult = request1.toString();
        assertTrue(stringResult.contains("S1"));
        assertTrue(stringResult.contains("CreateBookingRequest"));
    }
}