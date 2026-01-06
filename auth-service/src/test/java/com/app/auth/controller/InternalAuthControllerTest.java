package com.app.auth.controller;

import com.app.auth.dto.request.CreateTechnicianUserRequest;
import com.app.auth.dto.response.CreateTechnicianUserResponse;
import com.app.auth.dto.response.InternalUserResponse;
import com.app.auth.service.AuthService;
import com.app.auth.util.JwtUtil; // Import this
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InternalAuthController.class, properties = {"eureka.client.enabled=false"})
@AutoConfigureMockMvc(addFilters = false) // Filters are bypassed, but beans are still initialized
class InternalAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    // --- ADD THIS ---
    // This mocks the dependency required by JwtAuthenticationFilter
    // allowing the context to start.
    @MockitoBean
    private JwtUtil jwtUtil; 
    // ----------------

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTechnicianUser_success() throws Exception {
        CreateTechnicianUserRequest request = new CreateTechnicianUserRequest();
        request.setEmail("tech@example.com");

        CreateTechnicianUserResponse response = CreateTechnicianUserResponse.builder()
                .userId("U1")
                .email("tech@example.com")
                .temporaryPassword("temp1234")
                .role("TECHNICIAN")
                .build();

        when(authService.createTechnicianUser(any(CreateTechnicianUserRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/internal/auth/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("U1"))
                .andExpect(jsonPath("$.email").value("tech@example.com"))
                .andExpect(jsonPath("$.role").value("TECHNICIAN"));
    }

    @Test
    void getUserById_success() throws Exception {
        InternalUserResponse response = new InternalUserResponse(
                "U1", "test@example.com", "CUSTOMER");

        when(authService.getUserByIdInternal("U1")).thenReturn(response);

        mockMvc.perform(get("/internal/auth/users/U1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("U1"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }
}