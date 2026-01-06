package com.app.assignment.controller;

import com.app.assignment.dto.request.CancelAssignmentRequest;
import com.app.assignment.dto.request.CreateAssignmentRequest;
import com.app.assignment.dto.request.ReassignAssignmentRequest;
import com.app.assignment.dto.response.AssignmentResponse;
import com.app.assignment.service.AssignmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.app.assignment.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate; // Import this

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminAssignmentController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "eureka.client.enabled=false",
    "eureka.client.register-with-eureka=false",
    "eureka.client.fetch-registry=false",
    "spring.cloud.discovery.enabled=false" // Added to stop Eureka logs
})
class AdminAssignmentControllerTest {

    @MockitoBean
    private JwtUtil jwtUtil;
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssignmentService assignmentService;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------------- CREATE ASSIGNMENT ----------------

    @Test
    void createAssignment_success() throws Exception {
        CreateAssignmentRequest request = new CreateAssignmentRequest();
        request.setBookingId("B1");
        request.setTechnicianId("T1");
        // FIX: Add these fields to pass validation
        request.setServiceId("S1"); 
        request.setTimeSlot("10:00 AM - 12:00 PM");
        request.setScheduledDate(LocalDate.now()); 

        AssignmentResponse response = AssignmentResponse.builder()
                .assignmentId("A1")
                .bookingId("B1")
                .technicianId("T1")
                .build();

        when(assignmentService.createAssignment(any())).thenReturn(response);

        mockMvc.perform(post("/api/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.assignmentId").value("A1"))
                .andExpect(jsonPath("$.bookingId").value("B1"))
                .andExpect(jsonPath("$.technicianId").value("T1"));
    }

    // ---------------- CANCEL ASSIGNMENT ----------------

    @Test
    void cancelAssignment_success() throws Exception {
        CancelAssignmentRequest request = new CancelAssignmentRequest();
        request.setReason("Customer cancelled");

        AssignmentResponse response = AssignmentResponse.builder()
                .assignmentId("A1")
                .status(null)
                .build();

        when(assignmentService.cancelAssignment(eq("A1"), eq("Customer cancelled")))
                .thenReturn(response);

        mockMvc.perform(put("/api/assignments/A1/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignmentId").value("A1"));
    }

    // ---------------- REASSIGN ASSIGNMENT ----------------

    @Test
    void reassignAssignment_success() throws Exception {
        ReassignAssignmentRequest request = new ReassignAssignmentRequest();
        request.setTechnicianId("T2");
        request.setTechnicianUserId("U2");

        AssignmentResponse response = AssignmentResponse.builder()
                .assignmentId("A1")
                .technicianId("T2")
                .build();

        when(assignmentService.reassignAssignment(eq("A1"), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/assignments/A1/reassign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignmentId").value("A1"))
                .andExpect(jsonPath("$.technicianId").value("T2"));
    }
}