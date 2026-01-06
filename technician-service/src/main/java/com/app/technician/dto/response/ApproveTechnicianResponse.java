package com.app.technician.dto.response;

import com.app.technician.model.TechnicianStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveTechnicianResponse {

    private String technicianId;
    private String userId;
    private TechnicianStatus status;
    private String temporaryPassword;
    private String message;
}
