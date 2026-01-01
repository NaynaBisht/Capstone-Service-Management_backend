package com.app.assignment.model;

public enum AssignmentStatus {

	PENDING, // Assignment created, waiting for technician response
	ASSIGNED, // Technician accepted the assignment
	REJECTED, // Technician rejected the assignment
	IN_PROGRESS, // Technician started the job
	COMPLETED, // Job completed successfully
	CANCELLED // Booking cancelled / admin override
}
