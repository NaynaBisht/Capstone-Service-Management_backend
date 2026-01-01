package com.app.assignment.repository;

import com.app.assignment.model.Assignment;
import com.app.assignment.model.AssignmentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends MongoRepository<Assignment, String> {

	// One booking → one active assignment
	Optional<Assignment> findByBookingId(String bookingId);

	// Technician dashboard
	List<Assignment> findByTechnicianId(String technicianId);

	// Technician active jobs
	List<Assignment> findByTechnicianIdAndStatusIn(String technicianId, List<AssignmentStatus> statuses);

	// Admin / Manager views
	List<Assignment> findByStatus(AssignmentStatus status);

	// Reassignment support
	List<Assignment> findByBookingIdAndStatus(String bookingId, AssignmentStatus status);
}
