package com.app.technician.dto.response;

import com.app.technician.model.TechnicianStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApproveTechnicianResponseTest {

    @Test
    void testLombokFunctionality() {
        // 1. Test Builder
        ApproveTechnicianResponse response = ApproveTechnicianResponse.builder()
                .technicianId("tech-123")
                .userId("user-abc")
                .status(TechnicianStatus.APPROVED)
                .temporaryPassword("pass123")
                .message("Success")
                .build();

        assertEquals("tech-123", response.getTechnicianId());
        assertEquals("user-abc", response.getUserId());
        assertEquals(TechnicianStatus.APPROVED, response.getStatus());

        // 2. Test Setters and Getters (via NoArgsConstructor)
        ApproveTechnicianResponse response2 = new ApproveTechnicianResponse();
        response2.setTechnicianId("tech-123");
        response2.setUserId("user-abc");
        response2.setStatus(TechnicianStatus.APPROVED);
        response2.setTemporaryPassword("pass123");
        response2.setMessage("Success");

        assertEquals("tech-123", response2.getTechnicianId());
        
        // 3. Test AllArgsConstructor
        ApproveTechnicianResponse response3 = new ApproveTechnicianResponse(
            "tech-123", "user-abc", TechnicianStatus.APPROVED, "pass123", "Success"
        );
        assertEquals("tech-123", response3.getTechnicianId());

        // 4. Test Equals and HashCode
        assertEquals(response, response2); // Objects created via builder and setters should be equal
        assertEquals(response.hashCode(), response2.hashCode());
        
        // 5. Test toString
        assertNotNull(response.toString());
        assertTrue(response.toString().contains("ApproveTechnicianResponse"));
    }
}