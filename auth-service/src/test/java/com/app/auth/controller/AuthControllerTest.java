package com.app.auth.controller;

import com.app.auth.dto.request.LoginRequest;
import com.app.auth.dto.request.RegisterRequest;
import com.app.auth.dto.response.AuthResponse;
import com.app.auth.dto.response.InternalUserResponse;
import com.app.auth.service.AuthService;
import com.app.auth.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

// 1. Disable Eureka
@WebMvcTest(controllers = AuthController.class, properties = { "eureka.client.enabled=false" })
// 2. Disable Security Filters so 401 errors disappear
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AuthService authService;

	// 3. Mock JwtUtil to fix the "NoSuchBeanDefinitionException"
	@MockitoBean
	private JwtUtil jwtUtil;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void register_success() throws Exception {
		RegisterRequest request = new RegisterRequest("test@example.com", "password");

		doNothing().when(authService).register(any(RegisterRequest.class));

		mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
				.andExpect(content().string("User registered"));
	}

	@Test
	void login_success() throws Exception {
		LoginRequest request = new LoginRequest("test@example.com", "password");
		AuthResponse response = new AuthResponse("jwt-token", "CUSTOMER");

		when(authService.login(any(LoginRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value("jwt-token")).andExpect(jsonPath("$.role").value("CUSTOMER"));
	}

	@Test
	void getCurrentUser_unauthorized() throws Exception {
		// This exercises lines 38-40 in your source code where userId == null
		mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
	}

	@Test
	void createServiceManager_success() throws Exception {
		RegisterRequest request = new RegisterRequest("manager@example.com", "password");

		doNothing().when(authService).createServiceManager(any(RegisterRequest.class));

		mockMvc.perform(post("/api/auth/create-manager").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
				.andExpect(content().string("Service Manager created"));
	}
}