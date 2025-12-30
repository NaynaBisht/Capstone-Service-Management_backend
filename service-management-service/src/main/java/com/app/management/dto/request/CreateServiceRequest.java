package com.app.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateServiceRequest {

    @NotBlank
    private String name;

    private String description;

    @NotEmpty
    private List<ServiceCategoryRequest> categories;
}
