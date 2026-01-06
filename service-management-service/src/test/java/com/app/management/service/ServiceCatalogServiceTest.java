package com.app.management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.management.dto.request.CreateServiceRequest;
import com.app.management.dto.request.ServiceCategoryRequest;
import com.app.management.dto.request.UpdateServiceRequest;
import com.app.management.dto.response.ServiceResponse;
import com.app.management.model.ServiceCategory;
import com.app.management.model.ServiceEntity;
import com.app.management.repository.ServiceRepository;

@ExtendWith(MockitoExtension.class)
class ServiceCatalogServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private ServiceCatalogService serviceCatalogService;

    private ServiceEntity serviceEntity;
    private final String SERVICE_ID = "srv-123";

    @BeforeEach
    void setUp() {
        serviceEntity = ServiceEntity.builder()
                .id(SERVICE_ID)
                .name("Plumbing")
                .description("Pipe repair")
                .categories(List.of(new ServiceCategory("cat-1", "Standard", 50.0)))
                .active(true)
                .createdAt(Instant.now())
                .build();
    }

    // Create Service Tests

    @Test
    void createService_Success() {
        // Arrange
        CreateServiceRequest request = new CreateServiceRequest();
        request.setName("Plumbing");
        request.setDescription("Pipe repair");
        ServiceCategoryRequest catReq = new ServiceCategoryRequest();
        catReq.setName("Standard");
        catReq.setPrice(50.0);
        request.setCategories(List.of(catReq));

        when(serviceRepository.save(any(ServiceEntity.class))).thenReturn(serviceEntity);

        // Act
        ServiceResponse response = serviceCatalogService.createService(request);

        // Assert
        assertNotNull(response);
        assertEquals(SERVICE_ID, response.getId());
        assertEquals("Plumbing", response.getName());
        assertEquals(1, response.getCategories().size());
        verify(serviceRepository).save(any(ServiceEntity.class));
    }

    // Get Service Tests

    @Test
    void getAllServices_Success() {
        when(serviceRepository.findAll()).thenReturn(List.of(serviceEntity));

        List<ServiceResponse> responses = serviceCatalogService.getAllServices();

        assertFalse(responses.isEmpty());
        assertEquals(SERVICE_ID, responses.get(0).getId());
    }

    @Test
    void getServiceById_Success() {
        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(serviceEntity));

        ServiceResponse response = serviceCatalogService.getServiceById(SERVICE_ID);

        assertEquals(SERVICE_ID, response.getId());
    }

    @Test
    void getServiceById_NotFound() {
        when(serviceRepository.findById("invalid")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            serviceCatalogService.getServiceById("invalid")
        );
    }

    // Update Service Tests

    @Test
    void updateService_Success_AllFields() {
        // Arrange
        UpdateServiceRequest request = new UpdateServiceRequest();
        request.setName("Advanced Plumbing");
        request.setDescription("New Desc");
        ServiceCategoryRequest catReq = new ServiceCategoryRequest();
        catReq.setName("Premium");
        catReq.setPrice(100.0);
        request.setCategories(List.of(catReq));

        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(serviceEntity));
        when(serviceRepository.save(any(ServiceEntity.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        ServiceResponse response = serviceCatalogService.updateService(SERVICE_ID, request);

        // Assert
        assertEquals("Advanced Plumbing", response.getName());
        assertEquals("New Desc", response.getDescription());
        assertEquals("Premium", response.getCategories().get(0).getName());
    }

    @Test
    void updateService_Success_PartialUpdate() {
        // Arrange: Only updating Name, description remains null in request
        UpdateServiceRequest request = new UpdateServiceRequest();
        request.setName("Name Only Update");
        request.setCategories(Collections.emptyList());

        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(serviceEntity));
        when(serviceRepository.save(any(ServiceEntity.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        ServiceResponse response = serviceCatalogService.updateService(SERVICE_ID, request);

        // Assert
        assertEquals("Name Only Update", response.getName());
        // Description should remain unchanged from setUp()
        assertEquals("Pipe repair", response.getDescription());
    }
    
    @Test
    void updateService_Success_DescriptionUpdate() {
        // Arrange: Only updating Description
        UpdateServiceRequest request = new UpdateServiceRequest();
        request.setDescription("Updated Desc");
        request.setCategories(Collections.emptyList());

        when(serviceRepository.findById(SERVICE_ID)).thenReturn(Optional.of(serviceEntity));
        when(serviceRepository.save(any(ServiceEntity.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        ServiceResponse response = serviceCatalogService.updateService(SERVICE_ID, request);

        // Assert
        assertEquals("Plumbing", response.getName()); // Name Unchanged
        assertEquals("Updated Desc", response.getDescription());
    }

    @Test
    void updateService_NotFound() {
        UpdateServiceRequest request = new UpdateServiceRequest();
        when(serviceRepository.findById("invalid")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            serviceCatalogService.updateService("invalid", request)
        );
    }
}