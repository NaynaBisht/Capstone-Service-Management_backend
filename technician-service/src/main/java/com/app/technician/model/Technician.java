package com.app.technician.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "technicians")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Technician {

    @Id
    private String id;

    private String userId; // null until approved

    private String name;
    private String email;
    private String phone;
    private String city;

    private List<String> skillCategoryIds;

    private Map<String, String> documents;

    private Integer experienceYears;

    private TechnicianStatus status;
    private String rejectionReason;

    private AvailabilityStatus availability;

    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
}

