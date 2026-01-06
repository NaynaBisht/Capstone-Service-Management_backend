package com.app.notification.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.stereotype.Component;

import com.app.notification.client.AuthServiceClient;
import com.app.notification.dto.NotificationEvent;
import com.app.notification.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

	private final AuthServiceClient authServiceClient;
	private final EmailService emailService;

	@RabbitListener(queues = "${notification.queue}")
	public void handleNotification(NotificationEvent event) {

		log.info("Received notification event: {}", event.getEventType());

		String userId = event.getRecipient().getUserId();

		var user = authServiceClient.getUserById(userId);

		switch (event.getEventType()) {

		case BOOKING_CREATED -> emailService.sendBookingCreatedEmail(user.getEmail(), event.getData());

		case BOOKING_RESCHEDULED -> emailService.sendBookingRescheduledEmail(user.getEmail(), event.getData());

		case BOOKING_CANCELLED -> emailService.sendBookingCancelledEmail(user.getEmail(), event.getData());

		case ASSIGNMENT_ASSIGNED -> emailService.sendTechnicianAssignedEmail(user.getEmail(), event.getData());

		case ASSIGNMENT_STARTED -> emailService.sendAssignmentStartedEmail(user.getEmail(), event.getData());

		case ASSIGNMENT_COMPLETED -> emailService.sendAssignmentCompletedEmail(user.getEmail(), event.getData());
		}
	}
}
