package com.app.auth.dto.response;

import org.junit.jupiter.api.Test;

import com.app.auth.dto.response.CreateTechnicianUserResponse;

import static org.junit.jupiter.api.Assertions.*;

class CreateTechnicianUserResponseTest {

    @Test
    void testBuilderAndGetters() {
        CreateTechnicianUserResponse response = CreateTechnicianUserResponse.builder()
                .userId("U1")
                .email("tech@example.com")
                .temporaryPassword("temp1234")
                .role("TECHNICIAN")
                .build();

        assertNotNull(response);
        assertEquals("U1", response.getUserId());
        assertEquals("tech@example.com", response.getEmail());
        assertEquals("temp1234", response.getTemporaryPassword());
        assertEquals("TECHNICIAN", response.getRole());
    }

    @Test
    void testEqualsAndHashCode() {
        CreateTechnicianUserResponse r1 = CreateTechnicianUserResponse.builder()
                .userId("U1")
                .email("tech@example.com")
                .role("TECHNICIAN")
                .build();

        CreateTechnicianUserResponse r2 = CreateTechnicianUserResponse.builder()
                .userId("U1")
                .email("tech@example.com")
                .role("TECHNICIAN")
                .build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }
}

