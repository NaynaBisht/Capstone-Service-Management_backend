package com.app.assignment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReassignAssignmentRequest {

    @NotBlank
    private String technicianId;

    @NotBlank
    private String technicianUserId;

    private String reason; // optional audit
}

