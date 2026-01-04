package com.app.assignment.dto.notification;

import lombok.*;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private NotificationEventType eventType;

    private Recipient recipient;

    private Map<String, Object> data;

    private Instant timestamp;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Recipient {
        private String userId;
    }
}
