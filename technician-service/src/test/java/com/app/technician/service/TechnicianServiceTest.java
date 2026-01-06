package com.app.technician.service;

import com.app.technician.client.AuthServiceClient;
import com.app.technician.dto.auth.CreateTechnicianUserResponse;
import com.app.technician.dto.request.TechnicianOnboardRequest;
import com.app.technician.dto.response.ApproveTechnicianResponse;
import com.app.technician.dto.response.TechnicianOnboardResponse;
import com.app.technician.model.AvailabilityStatus;
import com.app.technician.model.SkillType;
import com.app.technician.model.Technician;
import com.app.technician.model.TechnicianStatus;
import com.app.technician.repository.TechnicianRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TechnicianServiceTest {

    @Mock
    private TechnicianRepository technicianRepository;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private TechnicianService technicianService;

    private Technician technician;
    private TechnicianOnboardRequest onboardRequest;

    @BeforeEach
    void setUp() {
        // Prepare common data objects
        technician = Technician.builder()
                .id("tech-123")
                .name("John Doe")
                .email("john@example.com")
                .status(TechnicianStatus.PENDING)
                .availability(AvailabilityStatus.UNAVAILABLE)
                .documents(new HashMap<>())
                .skills(List.of(SkillType.PLUMBING))
                .build();

        onboardRequest = new TechnicianOnboardRequest();
        onboardRequest.setName("John Doe");
        onboardRequest.setEmail("john@example.com");
        onboardRequest.setPhone("1234567890");
        onboardRequest.setCity("New York");
        onboardRequest.setSkills(List.of(SkillType.PLUMBING));
        onboardRequest.setExperienceYears(5);
    }

    // --- Onboard Technician Tests ---

    @Test
    void testOnboardTechnician_Success() {
        // Mock Repository behaviors
        when(technicianRepository.findByEmail(onboardRequest.getEmail())).thenReturn(Optional.empty());
        when(technicianRepository.save(any(Technician.class))).thenAnswer(invocation -> {
            Technician t = invocation.getArgument(0);
            t.setId("generated-id");
            return t;
        });

        // Execute
        TechnicianOnboardResponse response = technicianService.onboardTechnician(onboardRequest);

        // Verify
        assertNotNull(response);
        assertEquals("generated-id", response.getTechnicianId());
        assertEquals("Technician profile created. Verification pending.", response.getMessage());
        verify(technicianRepository).save(any(Technician.class));
    }

    @Test
    void testOnboardTechnician_EmailAlreadyExists() {
        when(technicianRepository.findByEmail(onboardRequest.getEmail())).thenReturn(Optional.of(technician));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            technicianService.onboardTechnician(onboardRequest);
        });

        assertEquals("Technician already registered with this email", exception.getMessage());
        verify(technicianRepository, never()).save(any(Technician.class));
    }

    // --- Upload Documents Tests ---

    @Test
    void testUploadDocuments_Success_BothFiles() {
        MultipartFile aadhar = mock(MultipartFile.class);
        MultipartFile certificate = mock(MultipartFile.class);
        when(aadhar.getOriginalFilename()).thenReturn("aadhar.pdf");
        when(certificate.getOriginalFilename()).thenReturn("cert.pdf");

        when(technicianRepository.findById("tech-123")).thenReturn(Optional.of(technician));

        technicianService.uploadDocuments("tech-123", aadhar, certificate);

        assertEquals("s3://mock-bucket/aadhar.pdf", technician.getDocuments().get("aadhaar"));
        assertEquals("s3://mock-bucket/cert.pdf", technician.getDocuments().get("certificate"));
        verify(technicianRepository).save(technician);
    }

    @Test
    void testUploadDocuments_Success_SingleFile() {
        // Test branch coverage: Check if 'if (certificate != null)' is skipped correctly
        MultipartFile aadhar = mock(MultipartFile.class);
        when(aadhar.getOriginalFilename()).thenReturn("aadhar.pdf");
        
        when(technicianRepository.findById("tech-123")).thenReturn(Optional.of(technician));

        technicianService.uploadDocuments("tech-123", aadhar, null);

        assertTrue(technician.getDocuments().containsKey("aadhaar"));
        assertFalse(technician.getDocuments().containsKey("certificate"));
    }

    @Test
    void testUploadDocuments_TechnicianNotFound() {
        when(technicianRepository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            technicianService.uploadDocuments("invalid-id", null, null)
        );
    }

    @Test
    void testUploadDocuments_NotPendingStatus() {
        technician.setStatus(TechnicianStatus.APPROVED);
        when(technicianRepository.findById("tech-123")).thenReturn(Optional.of(technician));

        Exception exception = assertThrows(IllegalStateException.class, () -> 
            technicianService.uploadDocuments("tech-123", null, null)
        );
        assertTrue(exception.getMessage().contains("only for PENDING technicians"));
    }

    // --- Approve Technician Tests ---

    @Test
    void testApproveTechnician_Success() {
        // Mock Auth Response
        CreateTechnicianUserResponse authResponse = new CreateTechnicianUserResponse();
        authResponse.setUserId("user-001");
        authResponse.setTemporaryPassword("temp-pass");

        when(technicianRepository.findById("tech-123")).thenReturn(Optional.of(technician));
        when(authServiceClient.createTechnicianUser("john@example.com")).thenReturn(authResponse);

        ApproveTechnicianResponse response = technicianService.approveTechnician("tech-123");

        assertEquals(TechnicianStatus.APPROVED, technician.getStatus());
        assertEquals("user-001", technician.getUserId());
        assertEquals(AvailabilityStatus.AVAILABLE, technician.getAvailability());
        assertEquals("temp-pass", response.getTemporaryPassword());
        verify(technicianRepository).save(technician);
    }

    @Test
    void testApproveTechnician_WrongStatus() {
        technician.setStatus(TechnicianStatus.REJECTED);
        when(technicianRepository.findById("tech-123")).thenReturn(Optional.of(technician));

        assertThrows(IllegalStateException.class, () -> 
            technicianService.approveTechnician("tech-123")
        );
    }

    // --- Reject Technician Tests ---

    @Test
    void testRejectTechnician_Success() {
        when(technicianRepository.findById("tech-123")).thenReturn(Optional.of(technician));

        technicianService.rejectTechnician("tech-123", "Bad docs");

        assertEquals(TechnicianStatus.REJECTED, technician.getStatus());
        assertEquals("Bad docs", technician.getRejectionReason());
        verify(technicianRepository).save(technician);
    }

    @Test
    void testRejectTechnician_NotFound() {
        when(technicianRepository.findById("invalid")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> technicianService.rejectTechnician("invalid", "reason"));
    }

    @Test
    void testRejectTechnician_NotPending() {
        technician.setStatus(TechnicianStatus.APPROVED);
        when(technicianRepository.findById("tech-123")).thenReturn(Optional.of(technician));
        assertThrows(IllegalStateException.class, () -> technicianService.rejectTechnician("tech-123", "reason"));
    }

    // --- Update Availability Tests ---

    @Test
    void testUpdateAvailability_Success() {
        technician.setStatus(TechnicianStatus.APPROVED);
        when(technicianRepository.findById("tech-123")).thenReturn(Optional.of(technician));

        technicianService.updateAvailability("tech-123", AvailabilityStatus.UNAVAILABLE);

        assertEquals(AvailabilityStatus.UNAVAILABLE, technician.getAvailability());
        verify(technicianRepository).save(technician);
    }

    @Test
    void testUpdateAvailability_NotApproved() {
        technician.setStatus(TechnicianStatus.PENDING); // Not Approved
        when(technicianRepository.findById("tech-123")).thenReturn(Optional.of(technician));

        assertThrows(IllegalStateException.class, () -> 
            technicianService.updateAvailability("tech-123", AvailabilityStatus.AVAILABLE)
        );
    }

    // --- Search Technicians Tests ---

    @Test
    void testSearchTechnicians_WithSkillFilter() {
        Technician t1 = new Technician();
        t1.setSkills(List.of(SkillType.PLUMBING));
        
        Technician t2 = new Technician();
        t2.setSkills(List.of(SkillType.ELECTRICAL));

        when(technicianRepository.findByCityAndStatusAndAvailability(any(), any(), any()))
            .thenReturn(List.of(t1, t2));

        // Filter for PLUMBING
        List<Technician> result = technicianService.searchTechnicians(
            SkillType.PLUMBING, "NY", AvailabilityStatus.AVAILABLE, TechnicianStatus.APPROVED
        );

        assertEquals(1, result.size());
        assertTrue(result.contains(t1));
    }

    @Test
    void testSearchTechnicians_WithoutSkillFilter() {
        // Test branch coverage: Check if 'if (skill != null)' logic works when null
        when(technicianRepository.findByCityAndStatusAndAvailability(any(), any(), any()))
            .thenReturn(List.of(technician));

        List<Technician> result = technicianService.searchTechnicians(
            null, "NY", AvailabilityStatus.AVAILABLE, TechnicianStatus.APPROVED
        );

        assertEquals(1, result.size());
    }

    @Test
    void testGetTechniciansByStatus() {
        when(technicianRepository.findByStatus(TechnicianStatus.PENDING)).thenReturn(List.of(technician));
        
        List<Technician> result = technicianService.getTechniciansByStatus(TechnicianStatus.PENDING);
        assertFalse(result.isEmpty());
    }

    // --- Get By User ID Tests ---

    @Test
    void testGetTechnicianByUserId_Success() {
        when(technicianRepository.findByUserId("user-001")).thenReturn(Optional.of(technician));
        Technician result = technicianService.getTechnicianByUserId("user-001");
        assertNotNull(result);
    }

    @Test
    void testGetTechnicianByUserId_NotFound() {
        when(technicianRepository.findByUserId("user-001")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> technicianService.getTechnicianByUserId("user-001"));
    }
}