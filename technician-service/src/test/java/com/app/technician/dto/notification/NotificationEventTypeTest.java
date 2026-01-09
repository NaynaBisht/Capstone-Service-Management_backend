package com.app.technician.dto.notification;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NotificationEventTypeTest {

    @Test
    void shouldContainTechnicianApproved() {
        assertEquals(NotificationEventType.TECHNICIAN_APPROVED,
                NotificationEventType.valueOf("TECHNICIAN_APPROVED"));
    }
}
