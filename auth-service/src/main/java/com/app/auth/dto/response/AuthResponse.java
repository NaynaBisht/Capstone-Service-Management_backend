package com.app.auth.dto.response;

public record AuthResponse(
        String token,
        String role
) {}
