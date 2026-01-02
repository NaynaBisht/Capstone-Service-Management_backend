package com.app.assignment.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateAssignmentRequest {

    private String bookingId;
    private String serviceId;
    private LocalDate scheduledDate;
    private String timeSlot;
}
