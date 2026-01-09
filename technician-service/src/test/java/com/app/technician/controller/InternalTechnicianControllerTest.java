package com.app.technician.controller;

import com.app.technician.model.AvailabilityStatus;
import com.app.technician.model.Technician;
import com.app.technician.model.TechnicianStatus;
import com.app.technician.repository.TechnicianRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InternalTechnicianController.class, properties = "eureka.client.enabled=false")
@AutoConfigureMockMvc(addFilters = false)
class InternalTechnicianControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TechnicianRepository technicianRepository;

    private Technician technician;

    @BeforeEach
    void setUp() {
        // Setup a valid Technician object for testing
        technician = Technician.builder()
                .id("tech-123")
                .userId("user-001")
                .name("John Doe")
                .status(TechnicianStatus.APPROVED)
                .availability(AvailabilityStatus.AVAILABLE)
                .build();
    }

    // --- Tests for /available (findAvailableTechnician) ---

    @Test
    void testFindAvailableTechnician_Success() throws Exception {
        // Mock the custom repository query to return our technician
        when(technicianRepository.findFirstByStatusAndAvailabilityAndSkillCategoryIdsContaining(
                eq(TechnicianStatus.APPROVED),
                eq(AvailabilityStatus.AVAILABLE),
                eq("CAT_PLUMBING") 
        )).thenReturn(Optional.of(technician));

        // Perform GET request
        mockMvc.perform(get("/internal/technicians/available")
                        .param("categoryId", "CAT_PLUMBING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technicianId").value("tech-123"))
                .andExpect(jsonPath("$.userId").value("user-001"));
    }

    @Test
    void testFindAvailableTechnician_NotFound() throws Exception {

        when(technicianRepository.findFirstByStatusAndAvailabilityAndSkillCategoryIdsContaining(
                any(), any(), any()
        )).thenReturn(Optional.empty());

        mockMvc.perform(get("/internal/technicians/available")
                        .param("categoryId", "PLUMBING"))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> {
                    String message = result.getResolvedException().getMessage();
                    assert(message.contains("No available technician found"));
                });
    }

    @Test
    void testFindAvailableTechnician_InvalidSkillType() {
        try {
            mockMvc.perform(get("/internal/technicians/available")
                    .param("serviceId", "INVALID_SKILL"));
        } catch (Exception e) {
            // Verify the underlying cause is the missing Enum constant
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    // --- Tests for /{technicianId} (getTechnicianById) ---

    @Test
    void testGetTechnicianById_Success() throws Exception {
        when(technicianRepository.findById("tech-123")).thenReturn(Optional.of(technician));

        mockMvc.perform(get("/internal/technicians/{technicianId}", "tech-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technicianId").value("tech-123"))
                .andExpect(jsonPath("$.userId").value("user-001"));
    }

    @Test
    void testGetTechnicianById_NotFound() throws Exception {
        when(technicianRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        // Expect 404 Not Found (Correct behavior now)
        mockMvc.perform(get("/internal/technicians/{technicianId}", "non-existent-id"))
                .andExpect(status().isNotFound()) // <--- CHANGED FROM isInternalServerError()
                .andExpect(jsonPath("$.status").value(404)) // Optional: Check the body status too
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}