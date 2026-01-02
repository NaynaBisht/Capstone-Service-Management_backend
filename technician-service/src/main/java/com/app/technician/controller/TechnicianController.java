package com.app.technician.controller;

import com.app.technician.dto.request.TechnicianOnboardRequest;
import com.app.technician.dto.response.TechnicianOnboardResponse;
import com.app.technician.service.TechnicianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
