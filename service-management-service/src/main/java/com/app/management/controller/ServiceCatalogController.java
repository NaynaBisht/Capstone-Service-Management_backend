package com.app.management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.management.dto.request.CreateServiceRequest;
import com.app.management.dto.request.UpdateServiceRequest;
import com.app.management.dto.response.ServiceResponse;
import com.app.management.service.ServiceCatalogService;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceCatalogController {

	private final ServiceCatalogService serviceCatalogService;

	// ADMIN only
	@PostMapping
	public ResponseEntity<ServiceResponse> createService(@Valid @RequestBody CreateServiceRequest request) {
		return ResponseEntity.ok(serviceCatalogService.createService(request));
	}

	// ALL authenticated users
	@GetMapping
	public ResponseEntity<List<ServiceResponse>> getAllServices() {
		return ResponseEntity.ok(serviceCatalogService.getAllServices());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ServiceResponse> getServiceById(@PathVariable String id) {
		return ResponseEntity.ok(serviceCatalogService.getServiceById(id));
	}
	
	// ADMIN only
	@PatchMapping("/{id}")
	public ResponseEntity<ServiceResponse> updateService(@PathVariable String id,
			@Valid @RequestBody UpdateServiceRequest request) {
		return ResponseEntity.ok(serviceCatalogService.updateService(id, request));
	}
}
