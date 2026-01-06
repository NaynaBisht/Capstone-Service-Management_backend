package com.app.assignment.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AssignmentTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Assignment assignment = new Assignment();

        assignment.setAssignmentId("A1");
        assignment.setBookingId("B1");
        assignment.setTechnicianId("T1");
        assignment.setTechnicianUserId("U1");
        assignment.setStatus(AssignmentStatus.PENDING);
        assignment.setAttemptCount(1);

        assertEquals("A1", assignment.getAssignmentId());
        assertEquals("B1", assignment.getBookingId());
        assertEquals("T1", assignment.getTechnicianId());
        assertEquals("U1", assignment.getTechnicianUserId());
        assertEquals(AssignmentStatus.PENDING, assignment.getStatus());
        assertEquals(1, assignment.getAttemptCount());
    }

    @Test
    void testAllArgsConstructor() {
        Instant now = Instant.now();

        Assignment assignment = new Assignment(
                "A1", "B1", "T1", "U1",
                AssignmentStatus.ASSIGNED,
                now, now, now, now, now,
                2, now, "Cancelled by admin"
        );

        assertEquals("A1", assignment.getAssignmentId());
        assertEquals("B1", assignment.getBookingId());
        assertEquals("T1", assignment.getTechnicianId());
        assertEquals("U1", assignment.getTechnicianUserId());
        assertEquals(AssignmentStatus.ASSIGNED, assignment.getStatus());
        assertEquals(2, assignment.getAttemptCount());
        assertEquals("Cancelled by admin", assignment.getCancelReason());
    }

    @Test
    void testBuilder() {
        Assignment assignment = Assignment.builder()
                .assignmentId("A1")
                .bookingId("B1")
                .technicianId("T1")
                .technicianUserId("U1")
                .status(AssignmentStatus.IN_PROGRESS)
                .attemptCount(0)
                .build();

        assertNotNull(assignment);
        assertEquals("A1", assignment.getAssignmentId());
        assertEquals("B1", assignment.getBookingId());
        assertEquals("T1", assignment.getTechnicianId());
        assertEquals("U1", assignment.getTechnicianUserId());
        assertEquals(AssignmentStatus.IN_PROGRESS, assignment.getStatus());
    }

    @Test
    void testEqualsAndHashCode() {
        Assignment a1 = Assignment.builder()
                .assignmentId("A1")
                .bookingId("B1")
                .technicianId("T1")
                .build();

        Assignment a2 = Assignment.builder()
                .assignmentId("A1")
                .bookingId("B1")
                .technicianId("T1")
                .build();

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
    }
}
