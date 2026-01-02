package com.app.technician.service;

import com.app.technician.dto.request.TechnicianOnboardRequest;
import com.app.technician.dto.response.TechnicianOnboardResponse;
import com.app.technician.model.AvailabilityStatus;
import com.app.technician.model.Technician;
import com.app.technician.model.TechnicianStatus;
import com.app.technician.repository.TechnicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class TechnicianService {

        private final TechnicianRepository technicianRepository;

        public TechnicianOnboardResponse onboardTechnician(
                        TechnicianOnboardRequest request) {

                // email uniqueness check
                technicianRepository.findByEmail(request.getEmail())
                                .ifPresent(existing -> {
                                        throw new IllegalArgumentException(
                                                        "Technician already registered with this email");
                                });

                Technician technician = Technician.builder()
                                .name(request.getName())
                                .email(request.getEmail())
                                .phone(request.getPhone())
                                .city(request.getCity())
                                .skills(request.getSkills())
                                .experienceYears(request.getExperienceYears())
                                .documents(new HashMap<>())
                                .status(TechnicianStatus.PENDING)
                                .availability(AvailabilityStatus.UNAVAILABLE)
                                .createdAt(LocalDateTime.now())
                                .build();

                Technician saved = technicianRepository.save(technician);

                return TechnicianOnboardResponse.builder()
                                .technicianId(saved.getId())
                                .status(saved.getStatus())
                                .message("Technician profile created. Verification pending.")
                                .build();
        }
}
