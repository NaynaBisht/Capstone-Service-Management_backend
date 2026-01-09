package com.app.technician.controller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.technician.dto.response.TechnicianAvailabilityResponse;
import com.app.technician.model.AvailabilityStatus;
import com.app.technician.model.Technician;
import com.app.technician.model.TechnicianStatus;
import com.app.technician.repository.TechnicianRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/technicians")
@RequiredArgsConstructor
public class InternalTechnicianController {

	private final TechnicianRepository technicianRepository;

	@GetMapping("/available")
	public TechnicianAvailabilityResponse findAvailableTechnician(@RequestParam String categoryId) {
		Technician technician = technicianRepository
				.findFirstByStatusAndAvailabilityAndSkillCategoryIdsContaining(TechnicianStatus.APPROVED,
						AvailabilityStatus.AVAILABLE, categoryId)
				.orElseThrow(() -> new RuntimeException("No available technician found"));

		return new TechnicianAvailabilityResponse(technician.getId(), technician.getUserId());
	}

	@GetMapping("/{technicianId}")
	public TechnicianAvailabilityResponse getTechnicianById(@PathVariable String technicianId) {
		Technician technician = technicianRepository.findById(technicianId)
				.orElseThrow(() -> new RuntimeException("Technician not found with ID: " + technicianId));

		return new TechnicianAvailabilityResponse(technician.getId(), technician.getUserId());
	}
}
