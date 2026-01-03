package com.app.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTechnicianUserResponse {
    private String userId;
    private String email;
    private String temporaryPassword;
    private String role;
}

