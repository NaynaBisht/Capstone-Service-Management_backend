package com.app.assignment.service;

import com.app.assignment.client.BookingServiceClient;
import com.app.assignment.client.TechnicianServiceClient;
import com.app.assignment.dto.notification.AssignmentEventPublisher;
import com.app.assignment.dto.request.CreateAssignmentRequest;
import com.app.assignment.dto.request.ReassignAssignmentRequest;
import com.app.assignment.dto.response.AssignmentResponse;
import com.app.assignment.exception.AssignmentNotFoundException;
import com.app.assignment.exception.InvalidAssignmentStateException;
import com.app.assignment.exception.UnauthorizedAssignmentAccessException;
import com.app.assignment.model.Assignment;
import com.app.assignment.model.AssignmentStatus;
import com.app.assignment.repository.AssignmentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

        @Mock
        private AssignmentRepository assignmentRepository;

        @Mock
        private BookingServiceClient bookingServiceClient;

        @Mock
        private TechnicianServiceClient technicianServiceClient;

        @Mock
        private AssignmentEventPublisher assignmentEventPublisher;

        @InjectMocks
        private AssignmentService assignmentService;

        @BeforeEach
        void setupSecurityContext() {
                Authentication authentication = mock(Authentication.class);
                lenient().when(authentication.getCredentials()).thenReturn("test-token");

                SecurityContext securityContext = mock(SecurityContext.class);
                lenient().when(securityContext.getAuthentication()).thenReturn(authentication);

                SecurityContextHolder.setContext(securityContext);
        }

        // ---------------------- CREATE ASSIGNMENT ----------------------

        @Test
        void createAssignment_success() {
                CreateAssignmentRequest request = new CreateAssignmentRequest();
                request.setBookingId("B1");
                request.setTechnicianId("T1");

                TechnicianServiceClient.TechnicianDTO tech = new TechnicianServiceClient.TechnicianDTO();
                tech.setTechnicianId("T1");
                tech.setUserId("U1");

                doNothing().when(bookingServiceClient).validateBooking("B1", "test-token");

                when(technicianServiceClient.getTechnicianById("T1")).thenReturn(tech);
                when(assignmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

                AssignmentResponse response = assignmentService.createAssignment(request);

                assertNotNull(response);
                assertEquals("B1", response.getBookingId());
                assertEquals("T1", response.getTechnicianId());
                assertEquals(AssignmentStatus.PENDING, response.getStatus());

                verify(bookingServiceClient).validateBooking("B1", "test-token");
                verify(assignmentEventPublisher).publish(any());
        }

        @Test
        void createAssignment_noTechnician_throwsException() {
                CreateAssignmentRequest request = new CreateAssignmentRequest();
                request.setBookingId("B1");
                request.setTechnicianId("");

                RuntimeException ex = assertThrows(RuntimeException.class,
                                () -> assignmentService.createAssignment(request));

                assertEquals("Please select a technician to proceed.", ex.getMessage());
        }

        // ---------------------- GET MY ASSIGNMENTS ----------------------

        @Test
        void getMyAssignments_success() {
                Assignment a1 = Assignment.builder()
                                .assignmentId("A1")
                                .bookingId("B1")
                                .technicianId("T1")
                                .technicianUserId("U1")
                                .status(AssignmentStatus.PENDING)
                                .createdAt(Instant.now())
                                .build();

                when(assignmentRepository.findByTechnicianUserId("U1")).thenReturn(List.of(a1));

                List<AssignmentResponse> result = assignmentService.getMyAssignments("U1");

                assertEquals(1, result.size());
                assertEquals("A1", result.get(0).getAssignmentId());
        }

        // ---------------------- ACCEPT ASSIGNMENT ----------------------

        @Test
        void acceptAssignment_success() {
                Assignment assignment = Assignment.builder()
                                .assignmentId("A1")
                                .technicianUserId("U1")
                                .status(AssignmentStatus.PENDING)
                                .build();

                when(assignmentRepository.findById("A1")).thenReturn(Optional.of(assignment));
                when(assignmentRepository.save(any())).thenReturn(assignment);

                AssignmentResponse response = assignmentService.acceptAssignment("A1", "U1");

                assertEquals(AssignmentStatus.ASSIGNED, response.getStatus());
        }

        @Test
        void acceptAssignment_wrongUser_throwsException() {
                Assignment assignment = Assignment.builder()
                                .assignmentId("A1")
                                .technicianUserId("U2")
                                .status(AssignmentStatus.PENDING)
                                .build();

                when(assignmentRepository.findById("A1")).thenReturn(Optional.of(assignment));

                assertThrows(UnauthorizedAssignmentAccessException.class,
                                () -> assignmentService.acceptAssignment("A1", "U1"));
        }

        @Test
        void acceptAssignment_invalidState_throwsException() {
                Assignment assignment = Assignment.builder()
                                .assignmentId("A1")
                                .technicianUserId("U1")
                                .status(AssignmentStatus.COMPLETED)
                                .build();

                when(assignmentRepository.findById("A1")).thenReturn(Optional.of(assignment));

                assertThrows(InvalidAssignmentStateException.class,
                                () -> assignmentService.acceptAssignment("A1", "U1"));
        }

        // ---------------------- REJECT ASSIGNMENT ----------------------

        @Test
        void rejectAssignment_success() {
                Assignment assignment = Assignment.builder()
                                .assignmentId("A1")
                                .technicianUserId("U1")
                                .status(AssignmentStatus.PENDING)
                                .attemptCount(null)
                                .build();

                when(assignmentRepository.findById("A1")).thenReturn(Optional.of(assignment));
                when(assignmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

                AssignmentResponse response = assignmentService.rejectAssignment("A1", "U1");

                assertEquals(AssignmentStatus.REJECTED, response.getStatus());
        }

        // ---------------------- START ASSIGNMENT ----------------------

        @Test
        void startAssignment_success() {
                Assignment assignment = Assignment.builder()
                                .assignmentId("A1")
                                .bookingId("B1")
                                .technicianId("T1")
                                .technicianUserId("U1")
                                .status(AssignmentStatus.ASSIGNED)
                                .build();

                BookingServiceClient.BookingDTO booking = new BookingServiceClient.BookingDTO();
                booking.setBookingId("B1");
                booking.setCustomerId("C1");
                booking.setServiceName("Plumbing");

                when(assignmentRepository.findById("A1")).thenReturn(Optional.of(assignment));
                when(bookingServiceClient.getBooking("B1", "test-token")).thenReturn(booking);
                when(assignmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

                AssignmentResponse response = assignmentService.startAssignment("A1", "U1");

                assertEquals(AssignmentStatus.IN_PROGRESS, response.getStatus());
                verify(assignmentEventPublisher).publish(any());
        }

        @Test
        void startAssignment_invalidState_throwsException() {
                Assignment assignment = Assignment.builder()
                                .assignmentId("A1")
                                .technicianUserId("U1")
                                .status(AssignmentStatus.PENDING)
                                .build();

                when(assignmentRepository.findById("A1")).thenReturn(Optional.of(assignment));

                assertThrows(InvalidAssignmentStateException.class,
                                () -> assignmentService.startAssignment("A1", "U1"));
        }

        // ---------------------- COMPLETE ASSIGNMENT ----------------------

        @Test
        void completeAssignment_success() {
                Assignment assignment = Assignment.builder()
                                .assignmentId("A1")
                                .bookingId("B1")
                                .technicianId("T1")
                                .technicianUserId("U1")
                                .status(AssignmentStatus.IN_PROGRESS)
                                .build();

                BookingServiceClient.BookingDTO booking = new BookingServiceClient.BookingDTO();
                booking.setBookingId("B1");
                booking.setCustomerId("C1");

                when(assignmentRepository.findById("A1")).thenReturn(Optional.of(assignment));
                when(bookingServiceClient.getBooking("B1", "test-token")).thenReturn(booking);
                when(assignmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

                AssignmentResponse response = assignmentService.completeAssignment("A1", "U1");

                assertEquals(AssignmentStatus.COMPLETED, response.getStatus());
                verify(assignmentEventPublisher).publish(any());
        }

        // ---------------------- CANCEL ASSIGNMENT ----------------------

        @Test
        void cancelAssignment_success() {
                Assignment assignment = Assignment.builder()
                                .assignmentId("A1")
                                .status(AssignmentStatus.PENDING)
                                .build();

                when(assignmentRepository.findById("A1")).thenReturn(Optional.of(assignment));
                when(assignmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

                AssignmentResponse response = assignmentService.cancelAssignment("A1", "Customer cancelled");

                assertEquals(AssignmentStatus.CANCELLED, response.getStatus());
        }

        @Test
        void cancelAssignment_alreadyCompleted_throwsException() {
                Assignment assignment = Assignment.builder()
                                .assignmentId("A1")
                                .status(AssignmentStatus.COMPLETED)
                                .build();

                when(assignmentRepository.findById("A1")).thenReturn(Optional.of(assignment));

                assertThrows(InvalidAssignmentStateException.class,
                                () -> assignmentService.cancelAssignment("A1", "N/A"));
        }

        // ---------------------- REASSIGN ASSIGNMENT ----------------------

        @Test
        void reassignAssignment_success() {
                Assignment assignment = Assignment.builder()
                                .assignmentId("A1")
                                .status(AssignmentStatus.PENDING)
                                .attemptCount(0)
                                .build();

                ReassignAssignmentRequest request = new ReassignAssignmentRequest();
                request.setTechnicianId("T2");
                request.setTechnicianUserId("U2");

                when(assignmentRepository.findById("A1")).thenReturn(Optional.of(assignment));
                when(assignmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

                AssignmentResponse response = assignmentService.reassignAssignment("A1", request);

                assertEquals(AssignmentStatus.PENDING, response.getStatus());
                assertEquals("T2", response.getTechnicianId());
        }

        @Test
        void reassignAssignment_invalidState_throwsException() {
                Assignment assignment = Assignment.builder()
                                .assignmentId("A1")
                                .status(AssignmentStatus.COMPLETED)
                                .build();

                when(assignmentRepository.findById("A1")).thenReturn(Optional.of(assignment));

                assertThrows(InvalidAssignmentStateException.class,
                                () -> assignmentService.reassignAssignment("A1", new ReassignAssignmentRequest()));
        }

        // ---------------------- NOT FOUND ----------------------

        @Test
        void assignmentNotFound_throwsException() {
                when(assignmentRepository.findById("A1")).thenReturn(Optional.empty());

                assertThrows(AssignmentNotFoundException.class,
                                () -> assignmentService.acceptAssignment("A1", "U1"));
        }
}
