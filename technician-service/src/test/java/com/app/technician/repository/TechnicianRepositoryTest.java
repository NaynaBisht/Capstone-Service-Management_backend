package com.app.technician.repository;

import com.app.technician.model.AvailabilityStatus;
import com.app.technician.model.SkillType;
import com.app.technician.model.Technician;
import com.app.technician.model.TechnicianStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class TechnicianRepositoryTest {

    @Autowired
    private TechnicianRepository technicianRepository;

    private Technician tech1;
    private Technician tech2;

    @BeforeEach
    void setUp() {
        // Clean DB before test
        technicianRepository.deleteAll();

        // Create Valid Technician
        tech1 = Technician.builder()
                .name("John Doe")
                .email("john@example.com")
                .userId("u-1")
                .city("New York")
                .status(TechnicianStatus.APPROVED)
                .availability(AvailabilityStatus.AVAILABLE)
                .skills(List.of(SkillType.PLUMBING))
                .build();

        // Create Unavailable/Pending Technician
        tech2 = Technician.builder()
                .name("Jane Smith")
                .email("jane@example.com")
                .city("New York")
                .status(TechnicianStatus.PENDING)
                .availability(AvailabilityStatus.UNAVAILABLE)
                .skills(List.of(SkillType.ELECTRICAL))
                .build();

        technicianRepository.saveAll(List.of(tech1, tech2));
    }

    @AfterEach
    void tearDown() {
        technicianRepository.deleteAll();
    }

    @Test
    void testFindByEmail() {
        Optional<Technician> found = technicianRepository.findByEmail("john@example.com");
        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getName());
    }

    @Test
    void testFindByStatus() {
        List<Technician> pendingTechs = technicianRepository.findByStatus(TechnicianStatus.PENDING);
        assertEquals(1, pendingTechs.size());
        assertEquals("Jane Smith", pendingTechs.get(0).getName());
    }

    @Test
    void testFindByUserId() {
        Optional<Technician> found = technicianRepository.findByUserId("u-1");
        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getName());
    }

    @Test
    void testFindByCityAndStatusAndAvailability() {
        List<Technician> results = technicianRepository.findByCityAndStatusAndAvailability(
                "New York", 
                TechnicianStatus.APPROVED, 
                AvailabilityStatus.AVAILABLE
        );

        assertEquals(1, results.size());
        assertEquals("John Doe", results.get(0).getName());
    }

    @Test
    void testFindFirstByStatusAndAvailabilityAndSkillsContaining() {
        // This tests the complex custom query for skills
        Optional<Technician> result = technicianRepository.findFirstByStatusAndAvailabilityAndSkillsContaining(
                TechnicianStatus.APPROVED,
                AvailabilityStatus.AVAILABLE,
                SkillType.PLUMBING
        );

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
    }

    @Test
    void testFindFirstByStatusAndAvailabilityAndSkillsContaining_NotFound() {
        Optional<Technician> result = technicianRepository.findFirstByStatusAndAvailabilityAndSkillsContaining(
                TechnicianStatus.APPROVED,
                AvailabilityStatus.AVAILABLE,
                SkillType.CARPENTRY // No tech has this skill
        );

        assertTrue(result.isEmpty());
    }
}