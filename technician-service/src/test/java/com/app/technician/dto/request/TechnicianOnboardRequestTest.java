package com.app.technician.dto.request;

import com.app.technician.model.SkillType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TechnicianOnboardRequestTest {

    private Validator validator;
    private TechnicianOnboardRequest request;

    @BeforeEach
    void setUp() {
        // Setup Jakarta Validator manually for Unit Testing
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Initialize a valid request object before each test
        request = new TechnicianOnboardRequest();
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPhone("9876543210");
        request.setCity("New York");
        request.setExperienceYears(5);
        
        List<SkillType> skills = new ArrayList<>();
        request.setSkills(skills); 
    }


    @Test
    void testValidRequest() {
        
        List<SkillType> validSkills = new ArrayList<>();
        validSkills.add(null);
        request.setSkills(validSkills);

        Set<ConstraintViolation<TechnicianOnboardRequest>> violations = validator.validate(request);
        
        assertTrue(violations.isEmpty(), "Valid request should not have violations");
    }

    @Test
    void testInvalidEmail() {
        request.setEmail("invalid-email-format");
        
        Set<ConstraintViolation<TechnicianOnboardRequest>> violations = validator.validate(request);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Please provide a valid email address")));
    }

    @Test
    void testInvalidPhonePattern() {
        request.setPhone("123"); // Too short
        Set<ConstraintViolation<TechnicianOnboardRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Phone number must be exactly 10 digits")));

        request.setPhone("abcdefghij"); // Non-numeric
        violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Phone number must be exactly 10 digits")));
    }

    @Test
    void testCityConstraints() {
        request.setCity("A"); // Too short (min=2)
        Set<ConstraintViolation<TechnicianOnboardRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("City name must be between 2 and 50 characters")));
    }

    @Test
    void testSkillsNotEmpty() {
        request.setSkills(new ArrayList<>()); // Empty list
        
        Set<ConstraintViolation<TechnicianOnboardRequest>> violations = validator.validate(request);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("At least one skill is required")));
    }

    @Test
    void testExperiencePositive() {
        request.setExperienceYears(-1); // Negative value
        
        Set<ConstraintViolation<TechnicianOnboardRequest>> violations = validator.validate(request);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Experience cannot be negative")));
    }

    @Test
    void testGettersAndSetters() {
        TechnicianOnboardRequest req = new TechnicianOnboardRequest();
        
        req.setName("Test Name");
        assertEquals("Test Name", req.getName());
        
        req.setEmail("test@test.com");
        assertEquals("test@test.com", req.getEmail());
        
        req.setExperienceYears(10);
        assertEquals(10, req.getExperienceYears());
        
        // ... repeat for all fields to hit 100% method coverage
    }

    @Test
    void testLombokMethods() {
        TechnicianOnboardRequest req1 = new TechnicianOnboardRequest();
        req1.setEmail("test@test.com");
        
        TechnicianOnboardRequest req2 = new TechnicianOnboardRequest();
        req2.setEmail("test@test.com");
        
        // Test equals()
        assertEquals(req1, req2);
        
        // Test hashCode()
        assertEquals(req1.hashCode(), req2.hashCode());
        
        // Test toString()
        assertNotNull(req1.toString());
    }
}