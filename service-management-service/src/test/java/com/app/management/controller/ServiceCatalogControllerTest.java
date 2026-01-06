package com.app.management.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.app.management.config.JwtUtil; // <--- 1. ADD THIS IMPORT
import com.app.management.dto.request.CreateServiceRequest;
import com.app.management.dto.request.ServiceCategoryRequest;
import com.app.management.dto.request.UpdateServiceRequest;
import com.app.management.dto.response.ServiceResponse;
import com.app.management.service.ServiceCatalogService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ServiceCatalogController.class)
@AutoConfigureMockMvc(addFilters = false) 
class ServiceCatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServiceCatalogService serviceCatalogService;

    // 2. FIX: Mock the missing JwtUtil bean so the filter can be created
    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createService_Success() throws Exception {
        CreateServiceRequest request = new CreateServiceRequest();
        request.setName("Electrical");
        request.setDescription("Wiring");
        ServiceCategoryRequest cat = new ServiceCategoryRequest();
        cat.setName("Basic");
        cat.setPrice(10.0);
        request.setCategories(List.of(cat));

        ServiceResponse response = ServiceResponse.builder()
                .id("1")
                .name("Electrical")
                .build();

        given(serviceCatalogService.createService(any(CreateServiceRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Electrical"));
    }

    @Test
    void getAllServices_Success() throws Exception {
        ServiceResponse response = ServiceResponse.builder().id("1").build();
        given(serviceCatalogService.getAllServices()).willReturn(List.of(response));

        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getServiceById_Success() throws Exception {
        ServiceResponse response = ServiceResponse.builder().id("1").name("Test").build();
        given(serviceCatalogService.getServiceById("1")).willReturn(response);

        mockMvc.perform(get("/api/services/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    void updateService_Success() throws Exception {
        UpdateServiceRequest request = new UpdateServiceRequest();
        request.setName("Updated Name");

        ServiceCategoryRequest categoryReq = new ServiceCategoryRequest();
        categoryReq.setName("Maintenance");
        categoryReq.setPrice(50.0);

        request.setCategories(List.of(categoryReq));

        ServiceResponse response = ServiceResponse.builder()
                .id("1")
                .name("Updated Name")
                .build();

        given(serviceCatalogService.updateService(eq("1"), any(UpdateServiceRequest.class)))
                .willReturn(response);

        mockMvc.perform(patch("/api/services/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }
}