package com.app.technician.dto.response;

import com.app.technician.model.TechnicianStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApproveTechnicianResponse {

    private String technicianId;
    private String userId;
    private TechnicianStatus status;
    private String message;
}
