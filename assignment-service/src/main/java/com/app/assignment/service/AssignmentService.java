package com.app.assignment.service;

import com.app.assignment.client.BookingServiceClient;
import com.app.assignment.client.TechnicianServiceClient;
import com.app.assignment.dto.request.CreateAssignmentRequest;
import com.app.assignment.dto.response.AssignmentResponse;
import com.app.assignment.model.Assignment;
import com.app.assignment.model.AssignmentStatus;
import com.app.assignment.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final BookingServiceClient bookingServiceClient;
    private final TechnicianServiceClient technicianServiceClient;

    public AssignmentResponse createAssignment(CreateAssignmentRequest request) {

        //Validate booking
        bookingServiceClient.validateBooking(request.getBookingId());

        //Fetch technician
        TechnicianServiceClient.TechnicianDTO technician =
                technicianServiceClient.findAvailableTechnician(request.getServiceId());

        //Create Assignment
        Assignment assignment = Assignment.builder()
                .assignmentId(UUID.randomUUID().toString())
                .bookingId(request.getBookingId())
                .technicianId(technician.getTechnicianId())
                .technicianUserId(technician.getUserId())
                .status(AssignmentStatus.PENDING)
                .attemptCount(0)
                .createdAt(Instant.now())
                .build();

        assignmentRepository.save(assignment);

        //Response
        return AssignmentResponse.builder()
                .assignmentId(assignment.getAssignmentId())
                .bookingId(assignment.getBookingId())
                .technicianId(assignment.getTechnicianId())
                .technicianUserId(assignment.getTechnicianUserId())
                .status(assignment.getStatus())
                .createdAt(assignment.getCreatedAt())
                .build();
    }
}
