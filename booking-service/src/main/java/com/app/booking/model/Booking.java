package com.app.booking.model;

import java.time.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "customer-bookings")
public class Booking {

    @Id
    private String id;

    private String bookingId;
    private String customerId;

    private String technicianId;

    // From dropdowns
    private String serviceId; 
    private String serviceName; 

    private String categoryId;
    private String categoryName; 

    private LocalDate scheduledDate;

    // Store exactly what UI sends
    private TimeSlot timeSlot; // "10:00 - 12:00"

    // Simple for MVP
    private Address serviceAddress;

    private String issueDescription;

    private PaymentMode paymentMode;

    private BookingStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime cancelledAt;

}
