package com.app.management.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class ServiceManagementModelTest {

    // ServiceCategory Tests

    @Test
    void testServiceCategory_AllArgsConstructorAndGetters() {
        // Test @AllArgsConstructor
        ServiceCategory category = new ServiceCategory("cat-1", "Standard", 150.0);

        // Test Getters
        assertEquals("cat-1", category.getCategoryId());
        assertEquals("Standard", category.getName());
        assertEquals(150.0, category.getPrice());
    }

    @Test
    void testServiceCategory_NoArgsConstructorAndSetters() {
        // Test @NoArgsConstructor
        ServiceCategory category = new ServiceCategory();

        // Test Setters
        category.setCategoryId("cat-2");
        category.setName("Premium");
        category.setPrice(200.0);

        // Verify with Getters
        assertEquals("cat-2", category.getCategoryId());
        assertEquals("Premium", category.getName());
        assertEquals(200.0, category.getPrice());
    }

    @Test
    void testServiceCategory_EqualsAndHashCode() {
        ServiceCategory cat1 = new ServiceCategory("id1", "Name", 100.0);
        ServiceCategory cat2 = new ServiceCategory("id1", "Name", 100.0); // Same values
        ServiceCategory cat3 = new ServiceCategory("id2", "Other", 50.0); // Different values

        // Test Equals
        assertEquals(cat1, cat2);
        assertNotEquals(cat1, cat3);

        // Test HashCode
        assertEquals(cat1.hashCode(), cat2.hashCode());
        assertNotEquals(cat1.hashCode(), cat3.hashCode());
    }

    @Test
    void testServiceCategory_ToString() {
        ServiceCategory category = new ServiceCategory("id1", "TestCat", 100.0);
        String result = category.toString();
        
        // Lombok toString usually includes the class name and field values
        assertNotNull(result);
        assertTrue(result.contains("ServiceCategory"));
        assertTrue(result.contains("TestCat"));
    }

    // ServiceEntity Tests

    @Test
    void testServiceEntity_Builder() {
        Instant now = Instant.now();
        ServiceCategory cat = new ServiceCategory("c1", "Basic", 10.0);
        List<ServiceCategory> categories = Collections.singletonList(cat);

        // Test @Builder
        ServiceEntity entity = ServiceEntity.builder()
                .id("srv-1")
                .name("Plumbing")
                .description("Fix pipes")
                .categories(categories)
                .active(true)
                .createdAt(now)
                .build();

        assertEquals("srv-1", entity.getId());
        assertEquals("Plumbing", entity.getName());
        assertEquals("Fix pipes", entity.getDescription());
        assertEquals(categories, entity.getCategories());
        assertTrue(entity.isActive());
        assertEquals(now, entity.getCreatedAt());
    }

    @Test
    void testServiceEntity_NoArgsConstructorAndSetters() {
        ServiceEntity entity = new ServiceEntity();
        Instant now = Instant.now();

        entity.setId("srv-2");
        entity.setName("Electrical");
        entity.setDescription("Wiring");
        entity.setCategories(Collections.emptyList());
        entity.setActive(false);
        entity.setCreatedAt(now);

        assertEquals("srv-2", entity.getId());
        assertEquals("Electrical", entity.getName());
        assertEquals("Wiring", entity.getDescription());
        assertTrue(entity.getCategories().isEmpty());
        assertEquals(false, entity.isActive()); // Explicitly check false
        assertEquals(now, entity.getCreatedAt());
    }

    @Test
    void testServiceEntity_AllArgsConstructor() {
        // Since you added @AllArgsConstructor, we should test it explicitly
        Instant now = Instant.now();
        ServiceEntity entity = new ServiceEntity(
            "srv-3", 
            "Cleaning", 
            "House Cleaning", 
            Collections.emptyList(), 
            true, 
            now
        );

        assertEquals("srv-3", entity.getId());
        assertEquals("Cleaning", entity.getName());
    }

    @Test
    void testServiceEntity_EqualsAndHashCode() {
        Instant now = Instant.now();
        ServiceEntity entity1 = ServiceEntity.builder().id("1").name("A").createdAt(now).build();
        ServiceEntity entity2 = ServiceEntity.builder().id("1").name("A").createdAt(now).build();
        ServiceEntity entity3 = ServiceEntity.builder().id("2").name("B").createdAt(now).build();

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
        assertNotEquals(entity1, entity3);
    }

    @Test
    void testServiceEntity_ToString() {
        ServiceEntity entity = ServiceEntity.builder().id("1").name("ToStringTest").build();
        String result = entity.toString();

        assertNotNull(result);
        assertTrue(result.contains("ServiceEntity"));
        assertTrue(result.contains("ToStringTest"));
    }
}