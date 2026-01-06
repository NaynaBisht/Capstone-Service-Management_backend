package com.app.technician.controller;

import com.app.technician.dto.request.RejectTechnicianRequest;
import com.app.technician.dto.request.TechnicianOnboardRequest;
import com.app.technician.dto.request.UpdateAvailabilityRequest;
import com.app.technician.dto.response.ApproveTechnicianResponse;
import com.app.technician.dto.response.TechnicianOnboardResponse;
import com.app.technician.model.AvailabilityStatus;
import com.app.technician.model.SkillType;
import com.app.technician.model.Technician;
import com.app.technician.model.TechnicianStatus;
import com.app.technician.repository.TechnicianRepository;
import com.app.technician.service.TechnicianService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TechnicianController.class, properties = "eureka.client.enabled=false")
@AutoConfigureMockMvc(addFilters = false) 
class TechnicianControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TechnicianService technicianService;

    @MockitoBean
    private TechnicianRepository technicianRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Technician technician;

    @BeforeEach
    void setUp() {
        technician = Technician.builder()
                .id("tech-123")
                .userId("user-001")
                .name("John Doe")
                .email("john@example.com")
                .status(TechnicianStatus.APPROVED)
                .availability(AvailabilityStatus.AVAILABLE)
                .skills(List.of(SkillType.PLUMBING))
                .city("New York")
                .experienceYears(5)
                .build();
    }

    @Test
    void testOnboardTechnician() throws Exception {
        TechnicianOnboardRequest request = new TechnicianOnboardRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPhone("1234567890");
        request.setCity("New York");
        request.setSkills(List.of(SkillType.PLUMBING));
        request.setExperienceYears(5);

        // Ensure your DTO has @NoArgsConstructor if you use 'new', 
        // otherwise use .builder() if available.
        TechnicianOnboardResponse response = new TechnicianOnboardResponse();
        response.setTechnicianId("tech-123");
        response.setStatus(TechnicianStatus.PENDING);

        when(technicianService.onboardTechnician(any(TechnicianOnboardRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/technicians/onboard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // This was failing with 403 previously
                .andExpect(jsonPath("$.technicianId").value("tech-123"));
    }

    @Test
    void testUploadDocuments() throws Exception {
        MockMultipartFile aadhar = new MockMultipartFile("aadhar", "aadhar.pdf", "application/pdf", "data".getBytes());
        MockMultipartFile certificate = new MockMultipartFile("certificate", "cert.pdf", "application/pdf", "data".getBytes());

        mockMvc.perform(multipart("/api/technicians/{technicianId}/documents", "tech-123")
                        .file(aadhar)
                        .file(certificate))
                .andExpect(status().isOk());

        verify(technicianService).uploadDocuments(eq("tech-123"), any(), any());
    }

    @Test
    void testGetTechniciansByStatus() throws Exception {
        when(technicianService.getTechniciansByStatus(TechnicianStatus.PENDING))
                .thenReturn(List.of(technician));

        mockMvc.perform(get("/api/technicians")
                        .param("status", "PENDING"))
                .andExpect(status().isOk()) // This was failing with 401 previously
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testApproveTechnician() throws Exception {
        ApproveTechnicianResponse response = new ApproveTechnicianResponse();
        response.setTechnicianId("tech-123");

        when(technicianService.approveTechnician("tech-123")).thenReturn(response);

        mockMvc.perform(post("/api/technicians/{technicianId}/approve", "tech-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technicianId").value("tech-123"));
    }

    @Test
    void testRejectTechnician() throws Exception {
        RejectTechnicianRequest request = new RejectTechnicianRequest();
        request.setReason("Incomplete docs");

        mockMvc.perform(post("/api/technicians/{technicianId}/reject", "tech-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(technicianService).rejectTechnician("tech-123", "Incomplete docs");
    }

    @Test
    void testUpdateAvailability() throws Exception {
        UpdateAvailabilityRequest request = new UpdateAvailabilityRequest();
        request.setAvailability(AvailabilityStatus.UNAVAILABLE);

        mockMvc.perform(patch("/api/technicians/{technicianId}/availability", "tech-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(technicianService).updateAvailability("tech-123", AvailabilityStatus.UNAVAILABLE);
    }

    @Test
    void testGetAllAvailableTechnicians() throws Exception {
        when(technicianRepository.findByStatusAndAvailability(TechnicianStatus.APPROVED, AvailabilityStatus.AVAILABLE))
                .thenReturn(List.of(technician));

        mockMvc.perform(get("/api/technicians/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].technicianId").value("tech-123")) 
                .andExpect(jsonPath("$[0].name").value("John Doe")); 
    }

    @Test
    void testSearchTechnicians() throws Exception {
        when(technicianService.searchTechnicians(SkillType.PLUMBING, "NY", AvailabilityStatus.AVAILABLE, TechnicianStatus.APPROVED))
                .thenReturn(List.of(technician));

        mockMvc.perform(get("/api/technicians/search")
                        .param("skill", "PLUMBING")
                        .param("city", "NY")
                        .param("availability", "AVAILABLE")
                        .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetByUserId() throws Exception {
        when(technicianService.getTechnicianByUserId("user-001")).thenReturn(technician);

        mockMvc.perform(get("/api/technicians/by-user/{userId}", "user-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user-001"));
    }
}