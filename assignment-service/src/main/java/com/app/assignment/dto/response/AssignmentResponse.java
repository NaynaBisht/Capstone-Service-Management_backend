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

	private String serviceId;
	private String categoryId;

	private AssignmentStatus status;

	private Instant createdAt;
	private Instant acceptedAt;
	private Instant startedAt;
	private Instant completedAt;
}
