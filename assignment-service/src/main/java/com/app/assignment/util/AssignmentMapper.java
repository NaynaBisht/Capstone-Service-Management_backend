package com.app.assignment.util;

import com.app.assignment.dto.response.AssignmentResponse;
import com.app.assignment.model.Assignment;

public class AssignmentMapper {

    private AssignmentMapper() {}

    public static AssignmentResponse toResponse(Assignment assignment) {

        return AssignmentResponse.builder()
                .assignmentId(assignment.getAssignmentId())
                .bookingId(assignment.getBookingId())
                .serviceId(assignment.getServiceId())
                .categoryId(assignment.getCategoryId())
                .status(assignment.getStatus())
                .createdAt(assignment.getCreatedAt())
                .acceptedAt(assignment.getAcceptedAt())
                .startedAt(assignment.getStartedAt())
                .completedAt(assignment.getCompletedAt())
                .build();
    }
}
