package com.app.auth.repository;

import com.app.auth.model.Role;
import com.app.auth.model.User;
import com.app.auth.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User createUser(String id, String email) {
        return User.builder()
                .id(id)
                .email(email)
                .passwordHash("hashed")
                .role(Role.CUSTOMER)
                .active(true)
                .createdAt(Instant.now())
                .build();
    }

    // ---------------- findByEmail ----------------

    @Test
    void findByEmail_success() {
        User user = createUser("U1", "test@example.com");
        userRepository.save(user);

        Optional<User> result = userRepository.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("U1", result.get().getId());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void findByEmail_notFound() {
        Optional<User> result = userRepository.findByEmail("notfound@example.com");

        assertFalse(result.isPresent());
    }

    // ---------------- existsByEmail ----------------

    @Test
    void existsByEmail_true() {
        User user = createUser("U2", "exists@example.com");
        userRepository.save(user);

        Boolean exists = userRepository.existsByEmail("exists@example.com");

        assertTrue(exists);
    }

    @Test
    void existsByEmail_false() {
        Boolean exists = userRepository.existsByEmail("missing@example.com");

        assertFalse(exists);
    }
}

