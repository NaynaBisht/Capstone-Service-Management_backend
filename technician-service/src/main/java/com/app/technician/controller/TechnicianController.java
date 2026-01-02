package com.app.technician.controller;

import com.app.technician.dto.request.RejectTechnicianRequest;
import com.app.technician.dto.request.TechnicianOnboardRequest;
import com.app.technician.dto.request.UpdateAvailabilityRequest;
import com.app.technician.dto.response.ApproveTechnicianResponse;
import com.app.technician.dto.response.TechnicianOnboardResponse;
import com.app.technician.model.Technician;
import com.app.technician.model.TechnicianStatus;
import com.app.technician.service.TechnicianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
    
    @GetMapping
    public List<Technician> getTechniciansByStatus(
            @RequestParam TechnicianStatus status
    ) {
        return technicianService.getTechniciansByStatus(status);
    }

    @PostMapping("/{technicianId}/approve")
    public ApproveTechnicianResponse approveTechnician(
            @PathVariable String technicianId
    ) {
        return technicianService.approveTechnician(technicianId);
    }

    @PostMapping("/{technicianId}/reject")
    public void rejectTechnician(
            @PathVariable String technicianId,
            @RequestBody RejectTechnicianRequest request
    ) {
        technicianService.rejectTechnician(
                technicianId,
                request.getReason()
        );
    }

    
    @PatchMapping("/{technicianId}/availability")
    public void updateAvailability(
            @PathVariable String technicianId,
            @RequestBody UpdateAvailabilityRequest request
    ) {
        technicianService.updateAvailability(
                technicianId,
                request.getAvailability()
        );
    }


}
