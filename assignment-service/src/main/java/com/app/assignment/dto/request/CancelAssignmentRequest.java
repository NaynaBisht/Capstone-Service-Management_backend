package com.app.assignment.dto.request;

import lombok.Data;

@Data
public class CancelAssignmentRequest {
    private String reason; // BOOKING_CANCELLED / ADMIN_OVERRIDE / SYSTEM
}

