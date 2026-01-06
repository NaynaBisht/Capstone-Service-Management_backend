package com.app.assignment.service;

import com.app.assignment.client.BookingServiceClient;

import com.app.assignment.client.TechnicianServiceClient;
import com.app.assignment.dto.notification.AssignmentEventPublisher;
import com.app.assignment.dto.notification.NotificationEvent;
import com.app.assignment.dto.notification.NotificationEventType;
import com.app.assignment.dto.request.CreateAssignmentRequest;
import com.app.assignment.dto.request.ReassignAssignmentRequest;
import com.app.assignment.dto.response.AssignmentResponse;
import com.app.assignment.exception.AssignmentNotFoundException;
import com.app.assignment.exception.InvalidAssignmentStateException;
import com.app.assignment.exception.UnauthorizedAssignmentAccessException;
import com.app.assignment.model.Assignment;
import com.app.assignment.model.AssignmentStatus;
import com.app.assignment.repository.AssignmentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentService {

        private final AssignmentRepository assignmentRepository;
        private final BookingServiceClient bookingServiceClient;
        private final TechnicianServiceClient technicianServiceClient;
        private final AssignmentEventPublisher assignmentEventPublisher;

        public AssignmentResponse createAssignment(CreateAssignmentRequest request) {

			String token = (String) org.springframework.security.core.context.SecurityContextHolder.getContext()
					.getAuthentication().getCredentials();

			// 1. Validate Booking
			bookingServiceClient.validateBooking(request.getBookingId(), token);

			// 2. Fetch Technician (CORRECTED LOGIC)
			// We declare the variable here, but don't assign it yet
			TechnicianServiceClient.TechnicianDTO technician; 

			if (request.getTechnicianId() != null && !request.getTechnicianId().isEmpty()) {
			    technician = technicianServiceClient.getTechnicianById(request.getTechnicianId());
			    } else {
				// CASE B: No technician selected (Auto-Assign)
				// If you still want this to work later, you need the "Enum Fix" we discussed.
				// For now, we throw an error if no technician is selected to avoid the crash.
				throw new RuntimeException("Please select a technician to proceed.");
			}

			// 3. Create Assignment (Using the 'technician' variable we just set)
			Assignment assignment = Assignment.builder().assignmentId(UUID.randomUUID().toString())
					.bookingId(request.getBookingId()).technicianId(technician.getTechnicianId()) // Now uses the
																									// correct
																									// technician
					.technicianUserId(technician.getUserId()).status(AssignmentStatus.PENDING).attemptCount(0)
					.createdAt(Instant.now()).build();

			assignmentRepository.save(assignment);

			// 4. Send Notification
			NotificationEvent event = NotificationEvent.builder().eventType(NotificationEventType.ASSIGNMENT_ASSIGNED)
					.recipient(new NotificationEvent.Recipient(technician.getUserId()))
					.data(Map.of("assignmentId", assignment.getAssignmentId(), "bookingId", assignment.getBookingId()))
					.timestamp(Instant.now()).build();

			assignmentEventPublisher.publish(event);

			// 5. Return Response
			return AssignmentResponse.builder().assignmentId(assignment.getAssignmentId())
					.bookingId(assignment.getBookingId()).technicianId(assignment.getTechnicianId())
					.technicianUserId(assignment.getTechnicianUserId()).status(assignment.getStatus())
					.createdAt(assignment.getCreatedAt()).build();
		}

        public List<AssignmentResponse> getMyAssignments(String technicianUserId) {

                List<Assignment> assignments = assignmentRepository.findByTechnicianUserId(technicianUserId);

                return assignments.stream()
                                .map(assignment -> AssignmentResponse.builder()
                                                .assignmentId(assignment.getAssignmentId())
                                                .bookingId(assignment.getBookingId())
                                                .technicianId(assignment.getTechnicianId())
                                                .technicianUserId(assignment.getTechnicianUserId())
                                                .status(assignment.getStatus())
                                                .createdAt(assignment.getCreatedAt())
                                                .build())
                                .toList();
        }

        // technician accepts an assignment
        public AssignmentResponse acceptAssignment(
                        String assignmentId,
                        String technicianUserId) {

                Assignment assignment = assignmentRepository.findById(assignmentId)
                                .orElseThrow(() -> new AssignmentNotFoundException(assignmentId));

                // Ownership check
                if (!assignment.getTechnicianUserId().equals(technicianUserId)) {
                        throw new UnauthorizedAssignmentAccessException(
                                        "You are not allowed to accept this assignment");
                }

                // State validation
                if (assignment.getStatus() != AssignmentStatus.PENDING) {
                        throw new InvalidAssignmentStateException(
                                        "Only PENDING assignments can be accepted");
                }

                // State transition
                assignment.setStatus(AssignmentStatus.ASSIGNED);
                assignment.setAcceptedAt(Instant.now());
                assignment.setUpdatedAt(Instant.now());

                assignmentRepository.save(assignment);

                // Response
                return AssignmentResponse.builder()
                                .assignmentId(assignment.getAssignmentId())
                                .bookingId(assignment.getBookingId())
                                .technicianId(assignment.getTechnicianId())
                                .technicianUserId(assignment.getTechnicianUserId())
                                .status(assignment.getStatus())
                                .createdAt(assignment.getCreatedAt())
                                .build();
        }

        // technician rejects an assignment
        public AssignmentResponse rejectAssignment(String assignmentId, String currentUserId) {

                Assignment assignment = assignmentRepository.findById(assignmentId)
                                .orElseThrow(() -> new AssignmentNotFoundException("Assignment not found"));

                // Ownership check
                if (!assignment.getTechnicianUserId().equals(currentUserId)) {
                        throw new UnauthorizedAssignmentAccessException(
                                        "You are not allowed to reject this assignment");
                }

                // State validation
                if (assignment.getStatus() != AssignmentStatus.PENDING) {
                        throw new InvalidAssignmentStateException(
                                        "Only PENDING assignments can be rejected");
                }

                // Business update
                assignment.setStatus(AssignmentStatus.REJECTED);
                assignment.setAttemptCount(
                                assignment.getAttemptCount() == null ? 1 : assignment.getAttemptCount() + 1);
                assignment.setUpdatedAt(Instant.now());

                Assignment saved = assignmentRepository.save(assignment);

                return AssignmentResponse.builder()
                                .assignmentId(saved.getAssignmentId())
                                .bookingId(saved.getBookingId())
                                .technicianId(saved.getTechnicianId())
                                .technicianUserId(saved.getTechnicianUserId())
                                .status(saved.getStatus())
                                .createdAt(saved.getCreatedAt())
                                .build();

        }

        // technician starts an assignment
        public AssignmentResponse startAssignment(
                        String assignmentId,
                        String technicianUserId) {

                Assignment assignment = assignmentRepository.findById(assignmentId)
                                .orElseThrow(() -> new AssignmentNotFoundException(assignmentId));

                // Ownership check
                if (!assignment.getTechnicianUserId().equals(technicianUserId)) {
                        throw new UnauthorizedAssignmentAccessException(
                                        "You are not allowed to start this assignment");
                }

                // State validation
                if (assignment.getStatus() != AssignmentStatus.ASSIGNED) {
                        throw new InvalidAssignmentStateException(
                                        "Only ACCEPTED assignments can be started");
                }

                String token = (String) org.springframework.security.core.context.SecurityContextHolder
                                .getContext().getAuthentication().getCredentials();

                BookingServiceClient.BookingDTO booking = bookingServiceClient.getBooking(assignment.getBookingId(),
                                token);

                // State transition
                assignment.setStatus(AssignmentStatus.IN_PROGRESS);
                assignment.setStartedAt(Instant.now());
                assignment.setUpdatedAt(Instant.now());

                Assignment saved = assignmentRepository.save(assignment);

                NotificationEvent event = NotificationEvent.builder()
                                .eventType(NotificationEventType.ASSIGNMENT_STARTED)
                                .recipient(new NotificationEvent.Recipient(
                                                booking.getCustomerId()))
                                .data(Map.of(
                                                "assignmentId", assignment.getAssignmentId(),
                                                "technicianId", assignment.getTechnicianId(),
                                                "bookingId", booking.getBookingId(),
                                                "serviceName",
                                                booking.getServiceName() != null ? booking.getServiceName()
                                                                : "Unknown Service"))
                                .timestamp(Instant.now())
                                .build();

                assignmentEventPublisher.publish(event);

                return AssignmentResponse.builder()
                                .assignmentId(saved.getAssignmentId())
                                .bookingId(saved.getBookingId())
                                .technicianId(saved.getTechnicianId())
                                .technicianUserId(saved.getTechnicianUserId())
                                .status(saved.getStatus())
                                .createdAt(saved.getCreatedAt())
                                .build();
        }

        // COMPLETE ASSIGNMENT
        public AssignmentResponse completeAssignment(
                        String assignmentId,
                        String technicianUserId) {

                Assignment assignment = assignmentRepository.findById(assignmentId)
                                .orElseThrow(() -> new AssignmentNotFoundException(assignmentId));

                // Ownership check
                if (!assignment.getTechnicianUserId().equals(technicianUserId)) {
                        throw new UnauthorizedAssignmentAccessException(
                                        "You are not allowed to complete this assignment");
                }

                // State validation
                if (assignment.getStatus() != AssignmentStatus.IN_PROGRESS) {
                        throw new InvalidAssignmentStateException(
                                        "Only IN_PROGRESS assignments can be completed");
                }
                String token = (String) org.springframework.security.core.context.SecurityContextHolder
                                .getContext().getAuthentication().getCredentials();

                BookingServiceClient.BookingDTO booking = bookingServiceClient.getBooking(assignment.getBookingId(),
                                token);
                // State transition
                assignment.setStatus(AssignmentStatus.COMPLETED);
                assignment.setCompletedAt(Instant.now());
                assignment.setUpdatedAt(Instant.now());

                Assignment saved = assignmentRepository.save(assignment);

                NotificationEvent event = NotificationEvent.builder()
                                .eventType(NotificationEventType.ASSIGNMENT_COMPLETED)
                                .recipient(new NotificationEvent.Recipient(
                                                booking.getCustomerId()))
                                .data(Map.of(
                                                "assignmentId", assignment.getAssignmentId(),
                                                "bookingId", booking.getBookingId(),
                                                "serviceName",
                                                booking.getServiceName() != null ? booking.getServiceName()
                                                                : "Unknown Service"))
                                .timestamp(Instant.now())
                                .build();

                assignmentEventPublisher.publish(event);

                return AssignmentResponse.builder()
                                .assignmentId(saved.getAssignmentId())
                                .bookingId(saved.getBookingId())
                                .technicianId(saved.getTechnicianId())
                                .technicianUserId(saved.getTechnicianUserId())
                                .status(saved.getStatus())
                                .createdAt(saved.getCreatedAt())
                                .build();
        }

        // cancel booking by admin or service manager
        public AssignmentResponse cancelAssignment(
                        String assignmentId,
                        String cancelReason) {

                Assignment assignment = assignmentRepository.findById(assignmentId)
                                .orElseThrow(() -> new AssignmentNotFoundException(assignmentId));

                // State validation
                if (assignment.getStatus() == AssignmentStatus.COMPLETED) {
                        throw new InvalidAssignmentStateException(
                                        "Completed assignments cannot be cancelled");
                }

                if (assignment.getStatus() == AssignmentStatus.CANCELLED) {
                        throw new InvalidAssignmentStateException(
                                        "Assignment is already cancelled");
                }

                // State transition
                assignment.setStatus(AssignmentStatus.CANCELLED);
                assignment.setCancelledAt(Instant.now());
                assignment.setCancelReason(cancelReason);
                assignment.setUpdatedAt(Instant.now());

                Assignment saved = assignmentRepository.save(assignment);

                return AssignmentResponse.builder()
                                .assignmentId(saved.getAssignmentId())
                                .bookingId(saved.getBookingId())
                                .technicianId(saved.getTechnicianId())
                                .technicianUserId(saved.getTechnicianUserId())
                                .status(saved.getStatus())
                                .createdAt(saved.getCreatedAt())
                                .build();
        }

        public AssignmentResponse reassignAssignment(
                        String assignmentId,
                        ReassignAssignmentRequest request) {
                Assignment assignment = assignmentRepository.findById(assignmentId)
                                .orElseThrow(() -> new AssignmentNotFoundException(assignmentId));

                AssignmentStatus status = assignment.getStatus();

                if (status != AssignmentStatus.PENDING &&
                                status != AssignmentStatus.REJECTED &&
                                status != AssignmentStatus.ASSIGNED) {

                        throw new InvalidAssignmentStateException(
                                        "Assignment cannot be reassigned in current state");
                }

                // Reassign technician
                assignment.setTechnicianId(request.getTechnicianId());
                assignment.setTechnicianUserId(request.getTechnicianUserId());

                // Reset lifecycle
                assignment.setStatus(AssignmentStatus.PENDING);
                assignment.setAcceptedAt(null);
                assignment.setStartedAt(null);

                assignment.setAttemptCount(
                                assignment.getAttemptCount() == null
                                                ? 1
                                                : assignment.getAttemptCount() + 1);

                assignment.setUpdatedAt(Instant.now());

                Assignment saved = assignmentRepository.save(assignment);

                return AssignmentResponse.builder()
                                .assignmentId(saved.getAssignmentId())
                                .bookingId(saved.getBookingId())
                                .technicianId(saved.getTechnicianId())
                                .technicianUserId(saved.getTechnicianUserId())
                                .status(saved.getStatus())
                                .createdAt(saved.getCreatedAt())
                                .build();
        }

}
