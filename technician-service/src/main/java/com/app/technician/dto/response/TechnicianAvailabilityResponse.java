package com.app.technician.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TechnicianAvailabilityResponse {
    private String technicianId;
    private String userId;
}
