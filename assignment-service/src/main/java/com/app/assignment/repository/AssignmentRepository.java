package com.app.assignment.repository;

import com.app.assignment.model.Assignment;
import com.app.assignment.model.AssignmentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends MongoRepository<Assignment, String> {

	// Technician: Get all assignments for logged-in technician
	List<Assignment> findByTechnicianUserId(String technicianUserId);

	// Technician: Get assignments by status (useful for filters later)
	List<Assignment> findByTechnicianUserIdAndStatus(String technicianUserId, AssignmentStatus status);

	// Admin/System: Fetch all assignments for a booking
	List<Assignment> findByBookingId(String bookingId);

	// System: Check if active assignment exists for booking
	Optional<Assignment> findFirstByBookingIdAndStatus(String bookingId, AssignmentStatus status);
}
