package com.app.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.auth.dto.request.CreateTechnicianUserRequest;
import com.app.auth.dto.response.CreateTechnicianUserResponse;
import com.app.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class InternalAuthController {

    private final AuthService authService;

    @PostMapping("/internal/auth/users")
    public CreateTechnicianUserResponse createTechnicianUser(
            @RequestBody CreateTechnicianUserRequest request
    ) {
        return authService.createTechnicianUser(request);
    }
}

