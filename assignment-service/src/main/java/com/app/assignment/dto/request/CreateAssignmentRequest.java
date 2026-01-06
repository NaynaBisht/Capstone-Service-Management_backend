package com.app.assignment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateAssignmentRequest {

    @NotBlank(message = "bookingId must not be null or blank")
    private String bookingId;

    @NotBlank(message = "serviceId must not be null or blank")
    private String serviceId;
    
    private String technicianId;

    @NotNull(message = "scheduledDate must not be null")
    @FutureOrPresent(message = "scheduledDate cannot be in the past")
    private LocalDate scheduledDate;

    @NotBlank(message = "timeSlot must not be null or blank")
    private String timeSlot;
}
