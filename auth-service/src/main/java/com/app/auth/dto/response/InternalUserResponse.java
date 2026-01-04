package com.app.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InternalUserResponse {
    private String userId;
    private String email;
    private String role;
}
