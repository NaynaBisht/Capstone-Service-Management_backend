package com.app.assignment.dto.response;

import com.app.assignment.model.AssignmentStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AssignmentResponseTest {

    @Test
    void testBuilderAndGetters() {
        Instant now = Instant.now();

        AssignmentResponse response = AssignmentResponse.builder()
                .assignmentId("A1")
                .bookingId("B1")
                .technicianId("T1")
                .technicianUserId("U1")
                .status(AssignmentStatus.COMPLETED)
                .createdAt(now)
                .build();

        assertNotNull(response);
        assertEquals("A1", response.getAssignmentId());
        assertEquals("B1", response.getBookingId());
        assertEquals("T1", response.getTechnicianId());
        assertEquals("U1", response.getTechnicianUserId());
        assertEquals(AssignmentStatus.COMPLETED, response.getStatus());
        assertEquals(now, response.getCreatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        AssignmentResponse r1 = AssignmentResponse.builder()
                .assignmentId("A1")
                .bookingId("B1")
                .build();

        AssignmentResponse r2 = AssignmentResponse.builder()
                .assignmentId("A1")
                .bookingId("B1")
                .build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }
}
