package com.app.booking.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

import com.app.booking.model.Address;
import com.app.booking.model.PaymentMode;
import com.app.booking.model.TimeSlot;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBookingRequest {

    // Service Catalog references
    @NotBlank(message = "Service ID is required")
    private String serviceId;

    @NotBlank(message = "Service name is required")
    private String serviceName;

    @NotBlank(message = "Category ID is required")
    private String categoryId;

    @NotBlank(message = "Category name is required")
    private String categoryName;

    // Schedule
    @NotNull(message = "Scheduled date is required")
    @FutureOrPresent(message = "Scheduled date cannot be in the past")
    private LocalDate scheduledDate;

    @NotNull(message = "Time slot is required")
    private TimeSlot timeSlot;

    // Address
    @NotNull(message = "Service address is required")
    private Address address;

    // Problem description
    @NotBlank(message = "Issue description is required")
    private String issueDescription;

    // Payment
    @NotNull(message = "Payment mode is required")
    private PaymentMode paymentMode;
}

