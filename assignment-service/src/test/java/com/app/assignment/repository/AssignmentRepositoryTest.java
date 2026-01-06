package com.app.assignment.repository;

import com.app.assignment.AssignmentServiceApplication;
import com.app.assignment.model.Assignment;
import com.app.assignment.model.AssignmentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ContextConfiguration(classes = AssignmentServiceApplication.class)
class AssignmentRepositoryTest {


    @Autowired
    private AssignmentRepository assignmentRepository;

    private Assignment createAssignment(String id, String bookingId, String technicianUserId, AssignmentStatus status) {
        return Assignment.builder()
                .assignmentId(id)
                .bookingId(bookingId)
                .technicianId("T1")
                .technicianUserId(technicianUserId)
                .status(status)
                .attemptCount(0)
                .createdAt(Instant.now())
                .build();
    }

    // ---------------- findByTechnicianUserId ----------------

    @Test
    void findByTechnicianUserId_success() {
        Assignment a1 = createAssignment("A1", "B1", "U1", AssignmentStatus.PENDING);
        Assignment a2 = createAssignment("A2", "B2", "U1", AssignmentStatus.ASSIGNED);
        Assignment a3 = createAssignment("A3", "B3", "U2", AssignmentStatus.PENDING);

        assignmentRepository.save(a1);
        assignmentRepository.save(a2);
        assignmentRepository.save(a3);

        List<Assignment> result = assignmentRepository.findByTechnicianUserId("U1");

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(a -> a.getAssignmentId().equals("A1")));
        assertTrue(result.stream().anyMatch(a -> a.getAssignmentId().equals("A2")));
    }

    // ---------------- findByTechnicianUserIdAndStatus ----------------

    @Test
    void findByTechnicianUserIdAndStatus_success() {
        Assignment a1 = createAssignment("A1", "B1", "U1", AssignmentStatus.PENDING);
        Assignment a2 = createAssignment("A2", "B2", "U1", AssignmentStatus.ASSIGNED);

        assignmentRepository.save(a1);
        assignmentRepository.save(a2);

        List<Assignment> result =
                assignmentRepository.findByTechnicianUserIdAndStatus("U1", AssignmentStatus.PENDING);

        assertEquals(1, result.size());
        assertEquals("A1", result.get(0).getAssignmentId());
    }

    // ---------------- findByBookingId ----------------

    @Test
    void findByBookingId_success() {
        Assignment a1 = createAssignment("A1", "B1", "U1", AssignmentStatus.PENDING);
        Assignment a2 = createAssignment("A2", "B1", "U2", AssignmentStatus.ASSIGNED);
        Assignment a3 = createAssignment("A3", "B2", "U3", AssignmentStatus.PENDING);

        assignmentRepository.save(a1);
        assignmentRepository.save(a2);
        assignmentRepository.save(a3);

        List<Assignment> result = assignmentRepository.findByBookingId("B1");

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(a -> a.getAssignmentId().equals("A1")));
        assertTrue(result.stream().anyMatch(a -> a.getAssignmentId().equals("A2")));
    }

    // ---------------- findFirstByBookingIdAndStatus ----------------

    @Test
    void findFirstByBookingIdAndStatus_success() {
        Assignment a1 = createAssignment("A1", "B1", "U1", AssignmentStatus.PENDING);
        Assignment a2 = createAssignment("A2", "B1", "U2", AssignmentStatus.ASSIGNED);

        assignmentRepository.save(a1);
        assignmentRepository.save(a2);

        Optional<Assignment> result =
                assignmentRepository.findFirstByBookingIdAndStatus("B1", AssignmentStatus.ASSIGNED);

        assertTrue(result.isPresent());
        assertEquals("A2", result.get().getAssignmentId());
    }

    @Test
    void findFirstByBookingIdAndStatus_notFound() {
        Assignment a1 = createAssignment("A1", "B1", "U1", AssignmentStatus.PENDING);
        assignmentRepository.save(a1);

        Optional<Assignment> result =
                assignmentRepository.findFirstByBookingIdAndStatus("B1", AssignmentStatus.COMPLETED);

        assertFalse(result.isPresent());
    }
}
