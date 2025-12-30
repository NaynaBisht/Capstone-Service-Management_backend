package com.app.management.model;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Document(collection = "services")
public class ServiceEntity {

    @Id
    private String id;

    private String name;               // Plumber
    private String description;

    private List<ServiceCategory> categories;

    private boolean active;

    private Instant createdAt;
}

