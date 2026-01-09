package com.app.auth.config;

import com.app.auth.model.Role;
import com.app.auth.model.User;
import com.app.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class AdminDataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-admin-password:}")
    private String defaultAdminPassword;

    @Bean
    public CommandLineRunner seedAdminUser() {
        return args -> {

            Optional<User> adminExists =
                    userRepository.findByEmail("admin@service.com");

            if (adminExists.isEmpty()) {

                if (defaultAdminPassword == null || defaultAdminPassword.isBlank()) {
                    System.out.println("Admin user not found. No default password set. Skipping creation.");
                    return;
                }

                User admin = User.builder()
                        .email("admin@service.com")
                        .passwordHash(passwordEncoder.encode(defaultAdminPassword))
                        .role(Role.ADMIN)
                        .active(true)
                        .createdAt(Instant.now())
                        .build();

                userRepository.save(admin);
                System.out.println("Admin user created.");
            } else {
                System.out.println("Admin user already exists.");
            }
        };
    }
}
