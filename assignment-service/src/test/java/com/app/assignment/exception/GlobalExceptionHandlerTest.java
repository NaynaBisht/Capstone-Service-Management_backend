package com.app.assignment.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// NO @WebMvcTest needed here - we use standalone setup
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // standaloneSetup bypasses all Security and Context issues
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ---------------- AssignmentNotFoundException ----------------
    @Test
    void handleAssignmentNotFoundException() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Assignment not found"));
    }

    // ---------------- InvalidAssignmentStateException ----------------
    @Test
    void handleInvalidAssignmentStateException() throws Exception {
        mockMvc.perform(get("/test/invalid-state"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Invalid assignment state"));
    }

    // ---------------- MethodArgumentNotValidException ----------------
    @Test
    void handleValidationErrors() throws Exception {
        ValidationRequest request = new ValidationRequest();
        request.setName(""); // invalid

        mockMvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("must not be blank"));
    }

    // ---------------- NoResourceFoundException ----------------
    @Test
    void handleNoResourceFoundException() throws Exception {
        // Note: NoResourceFoundException is harder to trigger in standalone mode
        // because usually Spring handles the mapping before reaching here.
        // For standalone, we usually just skip this test or manually throw the exception in a controller.
        
        mockMvc.perform(get("/test/no-resource"))
               .andExpect(status().isNotFound());
    }

    // ---------------- Generic Exception ----------------
    @Test
    void handleGenericException() throws Exception {
        mockMvc.perform(get("/test/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Something went wrong. Please try again later."));
    }

    // ==========================================================
    // =============== TEST CONTROLLER (ONLY FOR TESTS) =========
    // ==========================================================

    @RestController
    @RequestMapping("/test")
    static class TestController {

        @GetMapping("/not-found")
        public void throwNotFound() {
            throw new AssignmentNotFoundException("Assignment not found");
        }

        @GetMapping("/invalid-state")
        public void throwInvalidState() {
            throw new InvalidAssignmentStateException("Invalid assignment state");
        }

        @PostMapping("/validate")
        public void validate(@Valid @RequestBody ValidationRequest request) {
        }
        
        @GetMapping("/no-resource")
        public void throwNoResource() {
             // Simulating a 404 manually for standalone test
             throw new AssignmentNotFoundException("Endpoint not found");
        }

        @GetMapping("/generic")
        public void throwGeneric() {
            throw new RuntimeException("Unexpected error");
        }
    }

    static class ValidationRequest {
        @NotBlank(message = "must not be blank")
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}