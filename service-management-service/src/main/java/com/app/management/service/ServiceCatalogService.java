package com.app.management.service;

import org.springframework.stereotype.Service;

import com.app.management.dto.request.CreateServiceRequest;
import com.app.management.dto.response.ServiceResponse;
import com.app.management.model.ServiceCategory;
import com.app.management.model.ServiceEntity;
import com.app.management.repository.ServiceRepository;

import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceCatalogService {

    private final ServiceRepository serviceRepository;

    public ServiceResponse createService(CreateServiceRequest request) {

        List<ServiceCategory> categories = request.getCategories().stream()
                .map(cat -> new ServiceCategory(
                        UUID.randomUUID().toString(),
                        cat.getName(),
                        cat.getPrice()
                ))
                .toList();

        ServiceEntity service = ServiceEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .categories(categories)
                .active(true)
                .createdAt(Instant.now())
                .build();

        ServiceEntity saved = serviceRepository.save(service);

        return toResponse(saved);
    }
    
    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    public ServiceResponse getServiceById(String id) {
        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        return toResponse(service);
    }

    private ServiceResponse toResponse(ServiceEntity service) {
        return ServiceResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .categories(service.getCategories())
                .active(service.isActive())
                .build();
    } 
}

