package com.app.auth.service;

import com.app.auth.dto.request.CreateTechnicianUserRequest;
import com.app.auth.dto.request.LoginRequest;
import com.app.auth.dto.request.RegisterRequest;
import com.app.auth.dto.response.AuthResponse;
import com.app.auth.dto.response.CreateTechnicianUserResponse;
import com.app.auth.dto.response.InternalUserResponse;
import com.app.auth.model.Role;
import com.app.auth.model.User;
import com.app.auth.repository.UserRepository;
import com.app.auth.service.AuthService;
import com.app.auth.util.JwtUtil;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    // ---------------- REGISTER ----------------

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest("test@example.com", "password");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashed");

        assertDoesNotThrow(() -> authService.register(request));

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_emailAlreadyExists_throwsException() {
        RegisterRequest request = new RegisterRequest("test@example.com", "password");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.register(request));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Email already in use", ex.getReason());
    }

    // ---------------- LOGIN ----------------

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest("test@example.com", "password");

        User user = User.builder()
                .id("U1")
                .email("test@example.com")
                .passwordHash("hashed")
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken("U1", Role.CUSTOMER)).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals("CUSTOMER", response.role());
    }

    @Test
    void login_userNotFound_throwsException() {
        LoginRequest request = new LoginRequest("test@example.com", "password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.login(request));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assertEquals("Invalid credentials", ex.getReason());
    }

    @Test
    void login_wrongPassword_throwsException() {
        LoginRequest request = new LoginRequest("test@example.com", "password");

        User user = User.builder()
                .id("U1")
                .email("test@example.com")
                .passwordHash("hashed")
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hashed")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.login(request));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assertEquals("Invalid credentials", ex.getReason());
    }

    // ---------------- CREATE TECHNICIAN USER ----------------

    @Test
    void createTechnicianUser_success() {
        CreateTechnicianUserRequest request = new CreateTechnicianUserRequest();
        request.setEmail("tech@example.com");

        when(passwordEncoder.encode(any())).thenReturn("hashed");

        User saved = User.builder()
                .id("U1")
                .email("tech@example.com")
                .role(Role.TECHNICIAN)
                .active(true)
                .createdAt(Instant.now())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(saved);

        CreateTechnicianUserResponse response =
                authService.createTechnicianUser(request);

        assertNotNull(response);
        assertEquals("U1", response.getUserId());
        assertEquals("tech@example.com", response.getEmail());
        assertEquals("TECHNICIAN", response.getRole());
        assertNotNull(response.getTemporaryPassword());
    }

    // ---------------- CREATE SERVICE MANAGER ----------------

    @Test
    void createServiceManager_success() {
        RegisterRequest request = new RegisterRequest("manager@example.com", "password");

        when(userRepository.existsByEmail("manager@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashed");

        assertDoesNotThrow(() -> authService.createServiceManager(request));

        verify(userRepository).save(any(User.class));
    }

    @Test
    void createServiceManager_emailExists_throwsException() {
        RegisterRequest request = new RegisterRequest("manager@example.com", "password");

        when(userRepository.existsByEmail("manager@example.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.createServiceManager(request));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Email already in use", ex.getReason());
    }

    // ---------------- GET USER BY ID ----------------

    @Test
    void getUserByIdInternal_success() {
        User user = User.builder()
                .id("U1")
                .email("test@example.com")
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.findById("U1")).thenReturn(Optional.of(user));

        InternalUserResponse response = authService.getUserByIdInternal("U1");

        assertNotNull(response);
        assertEquals("U1", response.getUserId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("CUSTOMER", response.getRole());
    }

    @Test
    void getUserByIdInternal_notFound_throwsException() {
        when(userRepository.findById("U1")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.getUserByIdInternal("U1"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("User not found", ex.getReason());
    }
}

