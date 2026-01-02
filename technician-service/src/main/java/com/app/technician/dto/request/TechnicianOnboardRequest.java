package com.app.technician.dto.request;

import java.util.List;
import com.app.technician.model.SkillType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TechnicianOnboardRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 50, message = "City name must be between 2 and 50 characters")
    private String city;

    @NotEmpty(message = "At least one skill is required")
    private List<SkillType> skills;

    @NotNull(message = "Experience years is required")
    @Min(value = 0, message = "Experience cannot be negative")
    private Integer experienceYears;
}