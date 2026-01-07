package com.app.auth.config;

import com.app.auth.model.User;
import com.app.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDataInitializerTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminDataInitializer adminDataInitializer;

    @Test
    void shouldCreateAdminWhenNotExists() throws Exception {
        // Arrange: Repository returns empty
        when(userRepository.findByEmail("admin@service.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_pass");

        // Act
        CommandLineRunner runner = adminDataInitializer.seedAdminUser();
        runner.run();

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldNotCreateAdminWhenAlreadyExists() throws Exception {
        // Arrange: Repository returns an existing user
        when(userRepository.findByEmail("admin@service.com")).thenReturn(Optional.of(new User()));

        // Act
        CommandLineRunner runner = adminDataInitializer.seedAdminUser();
        runner.run();

        // Assert
        verify(userRepository, never()).save(any(User.class));
    }
}