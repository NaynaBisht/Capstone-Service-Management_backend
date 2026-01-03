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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TechnicianService {

        private final TechnicianRepository technicianRepository;
        private final AuthServiceClient authServiceClient;

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

        public void uploadDocuments(
                        String technicianId,
                        MultipartFile aadhar,
                        MultipartFile certificate) {

                Technician technician = technicianRepository.findById(technicianId)
                                .orElseThrow(() -> new IllegalArgumentException("Technician not found"));

                if (technician.getStatus() != TechnicianStatus.PENDING) {
                        throw new IllegalStateException(
                                        "Documents can be uploaded only for PENDING technicians");
                }

                if (aadhar != null) {
                        technician.getDocuments().put(
                                        "aadhaar",
                                        "s3://mock-bucket/" + aadhar.getOriginalFilename());
                }

                if (certificate != null) {
                        technician.getDocuments().put(
                                        "certificate",
                                        "s3://mock-bucket/" + certificate.getOriginalFilename());
                }

                technicianRepository.save(technician);
        }

        public List<Technician> getTechniciansByStatus(
                        TechnicianStatus status) {
                return technicianRepository.findByStatus(status);
        }

        public ApproveTechnicianResponse approveTechnician(String technicianId) {

                Technician technician = technicianRepository.findById(technicianId)
                                .orElseThrow(() -> new IllegalArgumentException("Technician not found"));

                if (technician.getStatus() != TechnicianStatus.PENDING) {
                        throw new IllegalStateException("Only PENDING technicians can be approved");
                }

                CreateTechnicianUserResponse authResponse =
                        authServiceClient.createTechnicianUser(technician.getEmail());

                technician.setUserId(authResponse.getUserId());
                technician.setStatus(TechnicianStatus.APPROVED);
                technician.setAvailability(AvailabilityStatus.AVAILABLE);
                technician.setApprovedAt(LocalDateTime.now());

                technicianRepository.save(technician);

                return ApproveTechnicianResponse.builder()
                        .technicianId(technician.getId())
                        .userId(authResponse.getUserId())
                        .temporaryPassword(authResponse.getTemporaryPassword())
                        .status(TechnicianStatus.APPROVED)
                        .message("Technician approved and account activated")
                        .build();
        }

        public void rejectTechnician(
                        String technicianId,
                        String reason) {

                Technician technician = technicianRepository.findById(technicianId)
                                .orElseThrow(() -> new IllegalArgumentException("Technician not found"));

                if (technician.getStatus() != TechnicianStatus.PENDING) {
                        throw new IllegalStateException(
                                        "Only PENDING technicians can be rejected");
                }

                technician.setStatus(TechnicianStatus.REJECTED);
                technician.setRejectionReason(reason);
                technician.setAvailability(AvailabilityStatus.UNAVAILABLE);

                technicianRepository.save(technician);
        }

        public void updateAvailability(
                        String technicianId,
                        AvailabilityStatus availability) {

                Technician technician = technicianRepository.findById(technicianId)
                                .orElseThrow(() -> new IllegalArgumentException("Technician not found"));

                if (technician.getStatus() != TechnicianStatus.APPROVED) {
                        throw new IllegalStateException(
                                        "Only APPROVED technicians can update availability");
                }

                technician.setAvailability(availability);
                technicianRepository.save(technician);
        }

        public List<Technician> searchTechnicians(
                        SkillType skill,
                        String city,
                        AvailabilityStatus availability,
                        TechnicianStatus status) {

                List<Technician> technicians = technicianRepository.findByCityAndStatusAndAvailability(
                                city,
                                status,
                                availability);

                // Filter by skill (Mongo array contains)
                if (skill != null) {
                        technicians = technicians.stream()
                                        .filter(t -> t.getSkills().contains(skill))
                                        .toList();
                }

                return technicians;
        }

        public Technician getTechnicianByUserId(String userId) {

                return technicianRepository.findByUserId(userId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Technician not found for userId: " + userId));
        }

}
