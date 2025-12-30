package com.app.management.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UpdateServiceRequest {

    private String name;
    private String description;

    @NotEmpty
    private List<ServiceCategoryRequest> categories;
}

