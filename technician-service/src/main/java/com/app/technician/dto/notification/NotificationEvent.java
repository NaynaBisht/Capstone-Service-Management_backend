package com.app.technician.dto.notification;

import java.time.Instant;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {

    private NotificationEventType eventType;
    private Recipient recipient;
    private Map<String, Object> data;
    private Instant timestamp;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Recipient {
        private String userId;
    }
}
