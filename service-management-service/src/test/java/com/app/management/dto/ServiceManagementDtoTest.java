package com.app.management.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.app.management.dto.request.CreateServiceRequest;
import com.app.management.dto.request.ServiceCategoryRequest;
import com.app.management.dto.request.UpdateServiceRequest;
import com.app.management.dto.response.ServiceResponse;
import com.app.management.model.ServiceCategory;
import com.app.management.model.ServiceEntity;

class ServiceManagementDtoTest {

    @Test
    void testServiceEntity() {
        ServiceEntity entity = ServiceEntity.builder()
                .id("1")
                .name("Service")
                .description("Desc")
                .categories(List.of())
                .active(true)
                .createdAt(Instant.now())
                .build();

        assertEquals("1", entity.getId());
        assertEquals("Service", entity.getName());
        assertTrue(entity.isActive());
        assertNotNull(entity.toString());
        
        ServiceEntity entity2 = new ServiceEntity();
        entity2.setId("1");
        entity2.setName("Service");
        entity2.setDescription("Desc");
        entity2.setCategories(List.of());
        entity2.setActive(true);
        entity2.setCreatedAt(entity.getCreatedAt());
        
        assertEquals(entity, entity2);
        assertEquals(entity.hashCode(), entity2.hashCode());
    }

    @Test
    void testServiceCategory() {
        ServiceCategory cat = new ServiceCategory("c1", "Cat1", 100.0);
        assertEquals("c1", cat.getCategoryId());
        assertEquals("Cat1", cat.getName());
        assertEquals(100.0, cat.getPrice());
        
        ServiceCategory cat2 = new ServiceCategory();
        cat2.setCategoryId("c1");
        cat2.setName("Cat1");
        cat2.setPrice(100.0);
        
        assertEquals(cat, cat2);
        assertTrue(cat.toString().contains("Cat1"));
    }

    @Test
    void testCreateServiceRequest() {
        CreateServiceRequest req = new CreateServiceRequest();
        req.setName("S1");
        req.setDescription("D1");
        req.setCategories(List.of());

        assertEquals("S1", req.getName());
        assertEquals("D1", req.getDescription());
        
        CreateServiceRequest req2 = new CreateServiceRequest();
        req2.setName("S1");
        req2.setDescription("D1");
        req2.setCategories(List.of());
        
        assertEquals(req, req2);
        assertNotNull(req.toString());
    }

    @Test
    void testUpdateServiceRequest() {
        UpdateServiceRequest req = new UpdateServiceRequest();
        req.setName("U1");
        req.setCategories(List.of());
        assertEquals("U1", req.getName());
        
        UpdateServiceRequest req2 = new UpdateServiceRequest();
        req2.setName("U1");
        req2.setCategories(List.of());
        
        assertEquals(req, req2);
    }
    
    @Test
    void testServiceCategoryRequest() {
        ServiceCategoryRequest req = new ServiceCategoryRequest();
        req.setName("C1");
        req.setPrice(50.0);
        
        assertEquals("C1", req.getName());
        assertEquals(50.0, req.getPrice());
        
        ServiceCategoryRequest req2 = new ServiceCategoryRequest();
        req2.setName("C1");
        req2.setPrice(50.0);
        
        assertEquals(req, req2);
    }

    @Test
    void testServiceResponse() {
        ServiceResponse res = ServiceResponse.builder()
                .id("R1")
                .name("Res")
                .active(true)
                .build();

        assertEquals("R1", res.getId());
        assertTrue(res.isActive());
        
        ServiceResponse res2 = new ServiceResponse();
        res2.setId("R1");
        res2.setName("Res");
        res2.setActive(true);
        
        assertEquals(res, res2);
        assertTrue(res.toString().contains("R1"));
    }
}