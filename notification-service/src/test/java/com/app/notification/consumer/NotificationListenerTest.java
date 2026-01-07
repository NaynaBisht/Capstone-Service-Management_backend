package com.app.notification.consumer;

import com.app.notification.client.AuthServiceClient;
import com.app.notification.client.AuthServiceClient.InternalUserResponse;
import com.app.notification.dto.NotificationEvent;
import com.app.notification.dto.NotificationEvent.Recipient;
import com.app.notification.dto.NotificationEventType;
import com.app.notification.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationListenerTest {

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationListener notificationListener;

    private InternalUserResponse mockUser;
    private final String userId = "user123";
    private final String userEmail = "test@example.com";
    private final Map<String, Object> eventData = Map.of("bookingId", "BK-999");

    @BeforeEach
    void setUp() {
        mockUser = new InternalUserResponse();
        mockUser.setEmail(userEmail);
        when(authServiceClient.getUserById(userId)).thenReturn(mockUser);
    }

    /**
     * Helper to create events with the correct Enum type
     */
    private NotificationEvent createEvent(NotificationEventType type) {
        NotificationEvent event = new NotificationEvent();
        event.setEventType(type);
        
        Recipient recipient = new Recipient();
        recipient.setUserId(userId);
        event.setRecipient(recipient);
        
        event.setData(eventData);
        return event;
    }

    @Test
    void handleNotification_BookingCreated() {
        NotificationEvent event = createEvent(NotificationEventType.BOOKING_CREATED);
        
        notificationListener.handleNotification(event);
        
        verify(emailService).sendBookingCreatedEmail(userEmail, eventData);
    }

    @Test
    void handleNotification_BookingRescheduled() {
        NotificationEvent event = createEvent(NotificationEventType.BOOKING_RESCHEDULED);
        
        notificationListener.handleNotification(event);
        
        verify(emailService).sendBookingRescheduledEmail(userEmail, eventData);
    }

    @Test
    void handleNotification_BookingCancelled() {
        NotificationEvent event = createEvent(NotificationEventType.BOOKING_CANCELLED);
        
        notificationListener.handleNotification(event);
        
        verify(emailService).sendBookingCancelledEmail(userEmail, eventData);
    }

    @Test
    void handleNotification_AssignmentAssigned() {
        NotificationEvent event = createEvent(NotificationEventType.ASSIGNMENT_ASSIGNED);
        
        notificationListener.handleNotification(event);
        
        verify(emailService).sendTechnicianAssignedEmail(userEmail, eventData);
    }

    @Test
    void handleNotification_AssignmentStarted() {
        NotificationEvent event = createEvent(NotificationEventType.ASSIGNMENT_STARTED);
        
        notificationListener.handleNotification(event);
        
        verify(emailService).sendAssignmentStartedEmail(userEmail, eventData);
    }

    @Test
    void handleNotification_AssignmentCompleted() {
        NotificationEvent event = createEvent(NotificationEventType.ASSIGNMENT_COMPLETED);
        
        notificationListener.handleNotification(event);
        
        verify(emailService).sendAssignmentCompletedEmail(userEmail, eventData);
    }
}