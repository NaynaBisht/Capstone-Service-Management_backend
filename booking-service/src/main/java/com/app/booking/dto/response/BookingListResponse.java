package com.app.booking.dto.response;

import lombok.*;
import java.time.*;

import com.app.booking.model.Address;
import com.app.booking.model.BookingStatus;
import com.app.booking.model.TimeSlot;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingListResponse {

    private String bookingId;
    private String customerId;

    private String serviceId;
    private String serviceName;
    private String categoryId;
    private String categoryName;

    private LocalDate scheduledDate;
    private TimeSlot timeSlot;

    private Address serviceAddress;
    private BookingStatus status;
    private LocalDateTime createdAt;
}
