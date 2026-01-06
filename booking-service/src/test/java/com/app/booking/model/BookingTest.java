package com.app.booking.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class BookingTest {

    @Test
    void testBookingBuilderAndGetters() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        Address address = new Address(); // Assuming Address exists with default constructor

        // Act
        Booking booking = Booking.builder()
                .id("1")
                .bookingId("BK-123")
                .customerId("cust-1")
                .technicianId("tech-1")
                .serviceId("srv-1")
                .serviceName("Repair")
                .categoryId("cat-1")
                .categoryName("Home")
                .scheduledDate(today)
                .timeSlot(TimeSlot.SLOT_11_13) // Assuming Enum
                .serviceAddress(address)
                .issueDescription("Broken")
                .paymentMode(PaymentMode.CASH) // Assuming Enum
                .status(BookingStatus.CONFIRMED) // Assuming Enum
                .createdAt(now)
                .updatedAt(now)
                .cancelledAt(null)
                .build();

        // Assert
        assertEquals("1", booking.getId());
        assertEquals("BK-123", booking.getBookingId());
        assertEquals("cust-1", booking.getCustomerId());
        assertEquals("tech-1", booking.getTechnicianId());
        assertEquals("srv-1", booking.getServiceId());
        assertEquals("Repair", booking.getServiceName());
        assertEquals("cat-1", booking.getCategoryId());
        assertEquals("Home", booking.getCategoryName());
        assertEquals(today, booking.getScheduledDate());
        assertEquals(TimeSlot.SLOT_11_13, booking.getTimeSlot());
        assertEquals(address, booking.getServiceAddress());
        assertEquals("Broken", booking.getIssueDescription());
        assertEquals(PaymentMode.CASH, booking.getPaymentMode());
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals(now, booking.getCreatedAt());
        assertEquals(now, booking.getUpdatedAt());
        assertNull(booking.getCancelledAt());
    }

    @Test
    void testBookingSettersAndNoArgsConstructor() {
        // Arrange
        Booking booking = new Booking();
        LocalDateTime now = LocalDateTime.now();

        // Act
        booking.setId("2");
        booking.setBookingId("BK-456");
        booking.setCreatedAt(now);

        // Assert
        assertEquals("2", booking.getId());
        assertEquals("BK-456", booking.getBookingId());
        assertEquals(now, booking.getCreatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Booking booking1 = Booking.builder().bookingId("BK-1").build();
        Booking booking2 = Booking.builder().bookingId("BK-1").build();
        Booking booking3 = Booking.builder().bookingId("BK-2").build();

        // Assert
        assertEquals(booking1, booking2); // Test equals
        assertEquals(booking1.hashCode(), booking2.hashCode()); // Test hashCode
        assertNotEquals(booking1, booking3); // Test inequality
    }

    @Test
    void testToString() {
        Booking booking = Booking.builder().bookingId("BK-STRING").build();
        String result = booking.toString();
        assertNotNull(result);
        assertTrue(result.contains("BK-STRING"));
        assertTrue(result.contains("Booking"));
    }
}