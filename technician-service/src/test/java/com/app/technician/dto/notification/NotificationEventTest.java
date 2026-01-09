package com.app.technician.dto.notification;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NotificationEventTest {

    @Test
    void shouldCreateAndReadFields() {
        NotificationEvent.Recipient recipient = new NotificationEvent.Recipient();
        recipient.setUserId("USER123");

        Map<String, Object> data = new HashMap<>();
        data.put("key", "value");

        Instant now = Instant.now();

        NotificationEvent event = new NotificationEvent();
        event.setEventType(NotificationEventType.TECHNICIAN_APPROVED);
        event.setRecipient(recipient);
        event.setData(data);
        event.setTimestamp(now);

        assertEquals(NotificationEventType.TECHNICIAN_APPROVED, event.getEventType());
        assertEquals("USER123", event.getRecipient().getUserId());
        assertEquals("value", event.getData().get("key"));
        assertEquals(now, event.getTimestamp());
    }
}
