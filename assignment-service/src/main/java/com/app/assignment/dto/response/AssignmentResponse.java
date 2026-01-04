package com.app.assignment.dto.response;

import com.app.assignment.model.AssignmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AssignmentResponse {

    private String assignmentId;
    private String bookingId;

    private String technicianId;
    private String technicianUserId;

    private AssignmentStatus status;

    private Instant createdAt;
}
