package com.app.booking.dto.response;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.app.booking.dto.response.BookingDetailsResponse;
import com.app.booking.dto.response.BookingListResponse;
import com.app.booking.dto.response.BookingResponse;
import com.app.booking.model.Address;
import com.app.booking.model.BookingStatus;
import com.app.booking.model.PaymentMode;
import com.app.booking.model.TimeSlot;

class BookingResponseDtoTest {

    // BookingDetailsResponse Tests
    
    @Test
    void testBookingDetailsResponse() {
        // Arrange
        BookingDetailsResponse response = BookingDetailsResponse.builder()
                .bookingId("BK-DET-1")
                .customerId("C1")
                .serviceId("S1")
                .serviceName("SName")
                .categoryId("Cat1")
                .categoryName("CatName")
                .scheduledDate(LocalDate.now())
                .timeSlot(TimeSlot.SLOT_16_18)
                .serviceAddress(new Address())
                .issueDescription("Desc")
                .paymentMode(PaymentMode.CASH)
                .status(BookingStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .build();

        // Test Getters (via Builder)
        assertEquals("BK-DET-1", response.getBookingId());
        assertEquals("C1", response.getCustomerId());
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());

        // Test Setters & NoArgsConstructor
        BookingDetailsResponse empty = new BookingDetailsResponse();
        empty.setBookingId("BK-EMPTY");
        assertEquals("BK-EMPTY", empty.getBookingId());

        // Test toString
        assertTrue(response.toString().contains("BK-DET-1"));
        
        // Test Equals/HashCode
        BookingDetailsResponse copy = new BookingDetailsResponse(
                "BK-DET-1", "C1", "S1", "SName", "Cat1", "CatName", 
                response.getScheduledDate(), TimeSlot.SLOT_16_18, 
                response.getServiceAddress(), "Desc", PaymentMode.CASH, 
                BookingStatus.CONFIRMED, response.getCreatedAt());
        
        assertEquals(response, copy);
        assertEquals(response.hashCode(), copy.hashCode());
    }

    // ==========================================
    // BookingListResponse Tests
    // ==========================================

    @Test
    void testBookingListResponse() {
        // Arrange
        BookingListResponse response = BookingListResponse.builder()
                .bookingId("BK-LIST-1")
                .customerId("C1")
                .serviceId("S1")
                .serviceName("SName")
                .categoryId("Cat1")
                .categoryName("CatName")
                .scheduledDate(LocalDate.now())
                .timeSlot(TimeSlot.SLOT_16_18)
                .serviceAddress(new Address())
                .status(BookingStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build();

        // Assert
        assertEquals("BK-LIST-1", response.getBookingId());
        assertEquals(BookingStatus.COMPLETED, response.getStatus());

        // Test Setters
        BookingListResponse empty = new BookingListResponse();
        empty.setServiceName("New Service");
        assertEquals("New Service", empty.getServiceName());
        
        // Test toString
        assertNotNull(response.toString());

        // Test Equals
        BookingListResponse response2 = BookingListResponse.builder().bookingId("BK-LIST-1").build();
        BookingListResponse response3 = BookingListResponse.builder().bookingId("BK-DIFF").build();
        // Note: Equals depends on all fields, so unless all are null/equal, they won't match.
        // Simple check:
        assertNotEquals(response, response3);
    }

    // BookingResponse Tests

    @Test
    void testBookingResponse() {
        // Arrange
        BookingResponse response = BookingResponse.builder()
                .bookingId("BK-RES-1")
                .status("SUCCESS")
                .build();

        // Assert
        assertEquals("BK-RES-1", response.getBookingId());
        assertEquals("SUCCESS", response.getStatus());

        // Test Setter
        BookingResponse empty = new BookingResponse();
        empty.setStatus("FAILED");
        assertEquals("FAILED", empty.getStatus());

        // Test Equals/HashCode
        BookingResponse copy = new BookingResponse("BK-RES-1", "SUCCESS");
        assertEquals(response, copy);
        assertEquals(response.hashCode(), copy.hashCode());
        
        // Test toString
        assertTrue(response.toString().contains("SUCCESS"));
    }
}