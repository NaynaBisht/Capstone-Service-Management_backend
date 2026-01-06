package com.app.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.app.management.model.ServiceCategory;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse {

    private String id;
    private String name;
    private String description;
    private List<ServiceCategory> categories;
    private boolean active;
}