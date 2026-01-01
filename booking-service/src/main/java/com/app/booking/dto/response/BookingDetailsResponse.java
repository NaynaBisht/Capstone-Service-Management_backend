package com.app.booking.dto.response;

import lombok.*;
import java.time.*;
import com.app.booking.model.TimeSlot;
import com.app.booking.model.Address;
import com.app.booking.model.BookingStatus;
import com.app.booking.model.PaymentMode;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDetailsResponse {

    private String bookingId;
    private String customerId;

    // Service snapshot + reference
    private String serviceId;
    private String serviceName;
    private String categoryId;
    private String categoryName;

    // Schedule
    private LocalDate scheduledDate;
    private TimeSlot timeSlot;

    // Address
    private Address serviceAddress;

    private String issueDescription;
    private PaymentMode paymentMode;

    private BookingStatus status;
    private LocalDateTime createdAt;
}
