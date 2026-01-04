package com.app.notification.dto;

import java.time.Instant;
import java.util.Map;

import lombok.Data;

@Data
public class NotificationEvent {

    private NotificationEventType eventType;
    private Recipient recipient;
    private Map<String, Object> data;
    private Instant timestamp;

    @Data
    public static class Recipient {
        private String userId;
    }
}
