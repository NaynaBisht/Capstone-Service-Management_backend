package com.app.technician.dto.response;

import com.app.technician.model.TechnicianStatus;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TechnicianOnboardResponse {

    private String technicianId;
    private TechnicianStatus status;
    private String message;
}

