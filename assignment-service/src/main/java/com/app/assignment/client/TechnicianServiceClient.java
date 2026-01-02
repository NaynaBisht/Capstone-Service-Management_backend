package com.app.assignment.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
public class TechnicianServiceClient {

    public TechnicianDTO findAvailableTechnician(String serviceId) {
        // STUB: later real logic
        return new TechnicianDTO(
                "TECH_001",
                "USER_456"
        );
    }

    @Data
    @AllArgsConstructor
    public static class TechnicianDTO {
        private String technicianId;
        private String userId;
    }
}
