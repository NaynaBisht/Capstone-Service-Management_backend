package com.app.auth.config;

import com.app.auth.model.Role;
import com.app.auth.model.User;
import com.app.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    @Bean
    public CommandLineRunner seedAdminUser() {
        return args -> {

            Optional<User> adminExists =
                    userRepository.findByEmail("admin@service.com");

            if (adminExists.isEmpty()) {

                User admin = User.builder()
                        .email("admin@service.com")
                        .passwordHash(passwordEncoder.encode("Admin@123"))
                        .role(Role.ADMIN)
                        .active(true)
                        .createdAt(Instant.now())
                        .build();

                userRepository.save(admin);

                System.out.println("Admin user created: admin@service.com");
            } else {
                System.out.println("Admin user already exists");
            }
        };
    }
}
