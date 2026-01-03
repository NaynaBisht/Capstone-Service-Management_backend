package com.app.technician.client;

import com.app.technician.dto.auth.CreateTechnicianUserRequest;
import com.app.technician.dto.auth.CreateTechnicianUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AuthServiceClient {

    private final RestTemplate restTemplate;

    public CreateTechnicianUserResponse createTechnicianUser(String email) {

        CreateTechnicianUserRequest request =
                new CreateTechnicianUserRequest();
        request.setEmail(email);

        return restTemplate.postForObject(
                "http://localhost:8081/internal/auth/users",
                request,
                CreateTechnicianUserResponse.class
        );
    }
}
