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

//	@RabbitListener(queues = "${notification.queue}")
//	public void handleNotification(NotificationEvent event) {
//
//		log.info("Received notification event: {}", event.getEventType());
//
//		String userId = event.getRecipient().getUserId();
//
//		var user = authServiceClient.getUserById(userId);
//		
//		if (user == null || user.getEmail() == null) {
//            log.error("Could not send notification: User or Email not found for ID {}", userId);
//            return;
//        }
//
//		switch (event.getEventType()) {
//
//		case BOOKING_CREATED -> emailService.sendBookingCreatedEmail(user.getEmail(), event.getData());
//
//		case BOOKING_RESCHEDULED -> emailService.sendBookingRescheduledEmail(user.getEmail(), event.getData());
//
//		case BOOKING_CANCELLED -> emailService.sendBookingCancelledEmail(user.getEmail(), event.getData());
//
//		case ASSIGNMENT_ASSIGNED -> emailService.sendTechnicianAssignedEmail(user.getEmail(), event.getData());
//
//		case ASSIGNMENT_STARTED -> emailService.sendAssignmentStartedEmail(user.getEmail(), event.getData());
//
//		case ASSIGNMENT_COMPLETED -> emailService.sendAssignmentCompletedEmail(user.getEmail(), event.getData());
//		
//		case TECHNICIAN_APPROVED -> emailService.sendTechnicianApprovedEmail(user.getEmail(), event.getData());
//
//		}
//	}
	
	@RabbitListener(queues = "${notification.queue}")
	public void handleNotification(NotificationEvent event) {
	    try {
	        log.info("Received notification event: {}", event.getEventType());

	        String userId = event.getRecipient().getUserId();
	        var user = authServiceClient.getUserById(userId);

	        if (user == null || user.getEmail() == null) {
	            log.error("Could not send notification: User or Email not found for ID {}", userId);
	            return;
	        }

	        switch (event.getEventType()) {
	            case BOOKING_CREATED -> emailService.sendBookingCreatedEmail(user.getEmail(), event.getData());
	            case BOOKING_RESCHEDULED -> emailService.sendBookingRescheduledEmail(user.getEmail(), event.getData());
	            case BOOKING_CANCELLED -> emailService.sendBookingCancelledEmail(user.getEmail(), event.getData());
	            case ASSIGNMENT_ASSIGNED -> emailService.sendTechnicianAssignedEmail(user.getEmail(), event.getData());
	            case ASSIGNMENT_STARTED -> emailService.sendAssignmentStartedEmail(user.getEmail(), event.getData());
	            case ASSIGNMENT_COMPLETED -> emailService.sendAssignmentCompletedEmail(user.getEmail(), event.getData());
	            case TECHNICIAN_APPROVED -> {
	                log.info("Triggering approval email for: {}", user.getEmail()); // FIXED 'L' to 'l'
	                emailService.sendTechnicianApprovedEmail(user.getEmail(), event.getData());
	            }
	            default -> log.warn("No email logic defined for event type: {}", event.getEventType());
	        }
	    } catch (Exception e) {
	        log.error("Failed to process notification event: {}", e.getMessage());
	    }
	}
}
