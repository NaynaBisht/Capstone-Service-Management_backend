package com.app.auth.service;

import com.app.auth.dto.request.CreateTechnicianUserRequest;
import com.app.auth.dto.request.LoginRequest;
import com.app.auth.dto.request.RegisterRequest;
import com.app.auth.dto.response.AuthResponse;
import com.app.auth.dto.response.CreateTechnicianUserResponse;
import com.app.auth.model.*;
import com.app.auth.repository.UserRepository;
import com.app.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;

        public void register(RegisterRequest request) {
                User user = User.builder()
                                .email(request.email())
                                .passwordHash(passwordEncoder.encode(request.password()))
                                .role(Role.CUSTOMER)
                                .active(true)
                                .createdAt(Instant.now())
                                .build();

                userRepository.save(user);
        }

        public AuthResponse login(LoginRequest request) {
                User user = userRepository.findByEmail(request.email())
                                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

                if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
                        throw new RuntimeException("Invalid credentials");
                }

                String token = jwtUtil.generateToken(user.getId(), user.getRole());
                return new AuthResponse(token, user.getRole().name());
        }

        public CreateTechnicianUserResponse createTechnicianUser(
                        CreateTechnicianUserRequest request) {
                String tempPassword = UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

                User user = User.builder()
                                .email(request.getEmail())
                                .passwordHash(passwordEncoder.encode(tempPassword))
                                .role(Role.TECHNICIAN)
                                .active(true)
                                .createdAt(Instant.now())
                                .build();

                User saved = userRepository.save(user);

                return CreateTechnicianUserResponse.builder()
                                .userId(saved.getId())
                                .email(saved.getEmail())
                                .temporaryPassword(tempPassword)
                                .role(saved.getRole().name())
                                .build();
        }

}
