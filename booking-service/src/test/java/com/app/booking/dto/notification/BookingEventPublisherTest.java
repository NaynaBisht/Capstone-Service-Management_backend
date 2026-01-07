package com.app.booking.dto.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Instant;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private BookingEventPublisher bookingEventPublisher;

    @Test
    void shouldPublishEventSuccessfully() {
        // Arrange
        NotificationEvent event = NotificationEvent.builder()
                .eventType(NotificationEventType.BOOKING_CREATED) // Replace with your actual enum constant
                .recipient(new NotificationEvent.Recipient("user-123"))
                .data(Map.of("bookingId", "BK-999"))
                .timestamp(Instant.now())
                .build();

        // Act
        bookingEventPublisher.publish(event);

        // Assert
        // Verifies the method call seen in line 20 of your source code
        verify(rabbitTemplate).convertAndSend(
                eq("notification.exchange"), 
                eq("notification.event"), 
                eq(event)
        );
    }
}