package com.app.assignment.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
public class Assignment {

    @Id
    private String assignmentId;

    private String bookingId;
    private String customerId;

    private String serviceId;
    private String categoryId;

    private String technicianId;

    private AssignmentStatus status;

    private Instant createdAt;
    private Instant updatedAt;

    private Instant acceptedAt;
    private Instant startedAt;
    private Instant completedAt;

    private Integer attemptCount; // reassignment attempts
}
