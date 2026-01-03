package com.app.technician.dto.auth;

import lombok.Data;

@Data
public class CreateTechnicianUserResponse {
    private String userId;
    private String email;
    private String temporaryPassword;
    private String role;
}

