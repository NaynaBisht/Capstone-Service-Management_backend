package com.app.management.service;

import org.springframework.stereotype.Service;

import com.app.management.dto.request.CreateServiceRequest;
import com.app.management.dto.request.ServiceCategoryRequest;
import com.app.management.dto.request.UpdateServiceRequest;
import com.app.management.dto.response.ServiceResponse;
import com.app.management.model.ServiceCategory;
import com.app.management.model.ServiceEntity;
import com.app.management.repository.ServiceRepository;

import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceCatalogService {

	private final ServiceRepository serviceRepository;

	public ServiceResponse createService(CreateServiceRequest request) {

		List<ServiceCategory> categories = request.getCategories().stream()
				.map(cat -> new ServiceCategory(UUID.randomUUID().toString(), cat.getName(), cat.getPrice())).toList();

		ServiceEntity service = ServiceEntity.builder().name(request.getName()).description(request.getDescription())
				.categories(categories).active(true).createdAt(Instant.now()).build();

		ServiceEntity saved = serviceRepository.save(service);

		return toResponse(saved);
	}

	public List<ServiceResponse> getAllServices() {
		return serviceRepository.findAll().stream().map(this::toResponse).toList();
	}

	public ServiceResponse getServiceById(String id) {
		ServiceEntity service = serviceRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Service not found"));
		return toResponse(service);
	}

	public ServiceResponse updateService(String id, UpdateServiceRequest request) {

		ServiceEntity service = serviceRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Service not found"));

		if (request.getName() != null) {
			service.setName(request.getName());
		}
		if (request.getDescription() != null) {
		    service.setDescription(request.getDescription());
		}
		
//		Map<String, ServiceCategory> existingMap =
//			    service.getCategories().stream()
//			        .collect(Collectors.toMap(ServiceCategory::getName, c -> c));
//
//		for (ServiceCategoryRequest requestCategory : request.getCategories()) {
//		    existingMap.put(
//		        requestCategory.getName(),
//		        new ServiceCategory(
//		            existingMap.containsKey(requestCategory.getName())
//		                ? existingMap.get(requestCategory.getName()).getCategoryId()
//		                : UUID.randomUUID().toString(),
//		            requestCategory.getName(),
//		            requestCategory.getPrice()
//		        )
//		    );
//		}

		List<ServiceCategory> categories = request.getCategories().stream()
				.map(cat -> new ServiceCategory(
						UUID.randomUUID().toString(), 
						cat.getName(), 
						cat.getPrice())).toList();

		service.setCategories(categories);

//		service.setCategories(new ArrayList<>(existingMap.values()));

		return toResponse(serviceRepository.save(service));
	}

	private ServiceResponse toResponse(ServiceEntity service) {
		return ServiceResponse.builder().id(service.getId()).name(service.getName())
				.description(service.getDescription()).categories(service.getCategories()).active(service.isActive())
				.build();
	}
}
