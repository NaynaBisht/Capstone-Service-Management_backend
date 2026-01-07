package com.app.technician.client;

import com.app.technician.dto.auth.CreateTechnicianUserRequest;
import com.app.technician.dto.auth.CreateTechnicianUserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthServiceClient authServiceClient;

    private final String testEmail = "test@example.com";
    private final String expectedUrl = "http://localhost:8081/internal/auth/users";

    @Test
    void createTechnicianUser_Success() {
        // 1. Arrange
        CreateTechnicianUserResponse mockResponse = new CreateTechnicianUserResponse();
        // Set any necessary fields on your response DTO here
        
        when(restTemplate.postForObject(
                eq(expectedUrl), 
                any(CreateTechnicianUserRequest.class), 
                eq(CreateTechnicianUserResponse.class))
        ).thenReturn(mockResponse);

        // 2. Act
        CreateTechnicianUserResponse result = authServiceClient.createTechnicianUser(testEmail);

        // 3. Assert & Verify
        assertNotNull(result);
        
        // ArgumentCaptor ensures the internal request object was built correctly
        ArgumentCaptor<CreateTechnicianUserRequest> requestCaptor = 
                ArgumentCaptor.forClass(CreateTechnicianUserRequest.class);
        
        verify(restTemplate).postForObject(eq(expectedUrl), requestCaptor.capture(), eq(CreateTechnicianUserResponse.class));
        
        assertEquals(testEmail, requestCaptor.getValue().getEmail());
    }
}