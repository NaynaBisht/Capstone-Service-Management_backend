package com.app.auth.dto.request;

import org.junit.jupiter.api.Test;

import com.app.auth.dto.request.CreateTechnicianUserRequest;

import static org.junit.jupiter.api.Assertions.*;

class CreateTechnicianUserRequestTest {

    @Test
    void testGettersAndSetters() {
        CreateTechnicianUserRequest request = new CreateTechnicianUserRequest();
        request.setEmail("tech@example.com");

        assertEquals("tech@example.com", request.getEmail());
    }

    @Test
    void testEqualsAndHashCode() {
        CreateTechnicianUserRequest r1 = new CreateTechnicianUserRequest();
        r1.setEmail("tech@example.com");

        CreateTechnicianUserRequest r2 = new CreateTechnicianUserRequest();
        r2.setEmail("tech@example.com");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }
}

