package com.app.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ServiceCategoryRequest {

    @NotBlank
    private String name;

    @Positive
    private double price;
}