package com.app.booking.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

import com.app.booking.model.TimeSlot;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RescheduleBookingRequest {

    @NotNull(message = "Scheduled date is required")
    @FutureOrPresent(message = "Scheduled date cannot be in the past")
    private LocalDate scheduledDate;

    @NotNull(message = "Time slot is required")
    private TimeSlot timeSlot;
}

