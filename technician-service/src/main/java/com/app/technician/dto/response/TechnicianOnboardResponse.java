package com.app.technician.dto.response;

import com.app.technician.model.TechnicianStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianOnboardResponse {

    private String technicianId;
    private TechnicianStatus status;
    private String message;
}

