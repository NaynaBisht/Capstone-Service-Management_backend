package com.app.technician.controller;

import com.app.technician.dto.request.TechnicianOnboardRequest;
import com.app.technician.dto.response.TechnicianOnboardResponse;
import com.app.technician.service.TechnicianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/technicians")
@RequiredArgsConstructor
public class TechnicianController {

    private final TechnicianService technicianService;

    @PostMapping("/onboard")
    public TechnicianOnboardResponse onboardTechnician(
            @Valid @RequestBody TechnicianOnboardRequest request) {
        return technicianService.onboardTechnician(request);
    }
    
    @PostMapping("/{technicianId}/documents")
    public void uploadDocuments(
            @PathVariable String technicianId,
            @RequestParam(required = false) MultipartFile aadhar,
            @RequestParam(required = false) MultipartFile certificate
    ) {
        technicianService.uploadDocuments(technicianId, aadhar, certificate);
    }

}
