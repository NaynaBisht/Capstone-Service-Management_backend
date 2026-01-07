package com.app.assignment.dto.notification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AssignmentEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private AssignmentEventPublisher assignmentEventPublisher;

    private final String exchange = "test-exchange";

    @BeforeEach
    void setUp() {
        // Manually inject the @Value field
        ReflectionTestUtils.setField(assignmentEventPublisher, "exchange", exchange);
    }

    @Test
    void publish_success() {
        // Arrange: Create a mock event with nested recipient data for the logger
        NotificationEvent event = new NotificationEvent();
        event.setEventType(NotificationEventType.ASSIGNMENT_ASSIGNED);
        
        NotificationEvent.Recipient recipient = new NotificationEvent.Recipient();
        recipient.setUserId("user-123");
        event.setRecipient(recipient);

        // Act
        assignmentEventPublisher.publish(event);

        // Assert: Verify rabbitTemplate.convertAndSend was called with correct parameters
        verify(rabbitTemplate).convertAndSend(
            eq(exchange), 
            eq("notification.event"), 
            any(NotificationEvent.class)
        );
    }
}