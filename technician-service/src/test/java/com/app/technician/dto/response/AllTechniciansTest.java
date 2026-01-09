package com.app.technician.dto.response;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AllTechniciansTest {

    @Test
    void testAllTechniciansRecord() {
        // Prepare Data
        String techId = "tech-123";
        String userId = "user-001";
        String name = "John Doe";

        // Use category IDs instead of SkillType enum
        Set<String> categoryIds = Set.of("CAT-PLUMBING", "CAT-ELECTRICAL");

        String city = "New York";
        int experience = 5;

        // 1. Test Constructor and Accessors
        AllTechnicians record = new AllTechnicians(
                techId,
                userId,
                name,
                categoryIds,
                city,
                experience
        );

        assertEquals(techId, record.technicianId());
        assertEquals(userId, record.userId());
        assertEquals(name, record.name());
        assertEquals(categoryIds, record.categoryIds());
        assertEquals(city, record.city());
        assertEquals(experience, record.experienceYears());

        // 2. Test Equals and HashCode
        AllTechnicians record2 = new AllTechnicians(
                techId,
                userId,
                name,
                categoryIds,
                city,
                experience
        );
        assertEquals(record, record2);
        assertEquals(record.hashCode(), record2.hashCode());

        // 3. Test Not Equals
        AllTechnicians record3 = new AllTechnicians(
                "diff-id",
                userId,
                name,
                categoryIds,
                city,
                experience
        );
        assertNotEquals(record, record3);

        // 4. Test toString (Ensure it contains the class name and data)
        String stringRepresentation = record.toString();
        assertTrue(stringRepresentation.contains("AllTechnicians"));
        assertTrue(stringRepresentation.contains("tech-123"));
    }
}
