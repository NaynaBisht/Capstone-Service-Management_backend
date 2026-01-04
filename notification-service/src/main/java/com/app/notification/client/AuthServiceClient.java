package com.app.notification.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthServiceClient {

    private final RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    public InternalUserResponse getUserById(String userId) {

        String url = authServiceUrl + "/internal/auth/users/" + userId;

        return restTemplate.getForObject(url, InternalUserResponse.class);
    }

    @Data
    public static class InternalUserResponse {
        private String userId;
        private String email;
        private String role;
    }
}
