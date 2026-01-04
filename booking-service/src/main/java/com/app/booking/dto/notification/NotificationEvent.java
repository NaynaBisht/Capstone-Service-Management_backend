package com.app.booking.dto.notification;

import java.time.Instant;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
