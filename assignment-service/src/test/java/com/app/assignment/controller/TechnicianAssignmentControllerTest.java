package com.app.assignment.controller;

import com.app.assignment.dto.response.AssignmentResponse;
import com.app.assignment.service.AssignmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.app.assignment.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; // IMPORT THIS
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(TechnicianAssignmentController.class)
@AutoConfigureMockMvc
@Import(SecurityAutoConfiguration.class) 
@TestPropertySource(properties = {
    "eureka.client.enabled=false",
    "eureka.client.register-with-eureka=false",
    "eureka.client.fetch-registry=false",
    "spring.cloud.discovery.enabled=false"
})
class TechnicianAssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;
    
    @MockitoBean
    private AssignmentService assignmentService;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------------- GET MY ASSIGNMENTS ----------------

    @Test
    @WithMockUser(username = "U1", roles = "TECHNICIAN")
    void getMyAssignments_success() throws Exception {
        AssignmentResponse response = AssignmentResponse.builder()
                .assignmentId("A1")
                .technicianUserId("U1")
                .build();

        when(assignmentService.getMyAssignments("U1")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/assignments/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assignmentId").value("A1"));
    }

    // ---------------- ACCEPT ASSIGNMENT ----------------

    @Test
    @WithMockUser(username = "U1", roles = "TECHNICIAN")
    void acceptAssignment_success() throws Exception {
        AssignmentResponse response = AssignmentResponse.builder()
                .assignmentId("A1")
                .build();

        when(assignmentService.acceptAssignment("A1", "U1")).thenReturn(response);

        // ADDED .with(csrf())
        mockMvc.perform(put("/api/assignments/A1/accept").with(csrf())) 
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignmentId").value("A1"));
    }

    // ---------------- REJECT ASSIGNMENT ----------------

    @Test
    @WithMockUser(username = "U1", roles = "TECHNICIAN")
    void rejectAssignment_success() throws Exception {
        AssignmentResponse response = AssignmentResponse.builder()
                .assignmentId("A1")
                .build();

        when(assignmentService.rejectAssignment("A1", "U1")).thenReturn(response);

        // ADDED .with(csrf())
        mockMvc.perform(put("/api/assignments/A1/reject").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignmentId").value("A1"));
    }

    // ---------------- START ASSIGNMENT ----------------

    @Test
    @WithMockUser(username = "U1", roles = "TECHNICIAN")
    void startAssignment_success() throws Exception {
        AssignmentResponse response = AssignmentResponse.builder()
                .assignmentId("A1")
                .build();

        when(assignmentService.startAssignment("A1", "U1")).thenReturn(response);

        // ADDED .with(csrf())
        mockMvc.perform(put("/api/assignments/A1/start").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignmentId").value("A1"));
    }

    // ---------------- COMPLETE ASSIGNMENT ----------------

    @Test
    @WithMockUser(username = "U1", roles = "TECHNICIAN")
    void completeAssignment_success() throws Exception {
        AssignmentResponse response = AssignmentResponse.builder()
                .assignmentId("A1")
                .build();

        when(assignmentService.completeAssignment("A1", "U1")).thenReturn(response);

        // ADDED .with(csrf())
        mockMvc.perform(put("/api/assignments/A1/complete").with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignmentId").value("A1"));
    }
}