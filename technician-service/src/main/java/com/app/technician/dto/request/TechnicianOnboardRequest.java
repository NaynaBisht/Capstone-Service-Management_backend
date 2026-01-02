package com.app.technician.dto.request;

import java.util.List;

import com.app.technician.model.SkillType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class TechnicianOnboardRequest {

    @NotBlank
    private String name;

    @Email
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String city;

    @NotEmpty
    private List<SkillType> skills;

    private Integer experienceYears;
}

