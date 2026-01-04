package com.app.assignment.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
@Document(collection = "assignments")
@CompoundIndexes({
        @CompoundIndex(
                name = "booking_idx",
                def = "{'bookingId': 1}"
        ),
        @CompoundIndex(
                name = "technician_status_idx",
                def = "{'technicianId': 1, 'status': 1}"
        )
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {

    @Id
    private String assignmentId;

    private String bookingId;

    private String technicianId;
    private String technicianUserId;

    private AssignmentStatus status;

    private Instant createdAt;
    private Instant updatedAt;

    private Instant acceptedAt;
    private Instant startedAt;
    private Instant completedAt;

    private Integer attemptCount;
    
    private Instant cancelledAt;
    private String cancelReason;

}
