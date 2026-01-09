package com.app.technician.dto.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.lang.reflect.Field;

import static org.mockito.Mockito.verify;

class NotificationEventPublisherTest {

    private RabbitTemplate rabbitTemplate;
    private NotificationEventPublisher publisher;

    @BeforeEach
    void setUp() throws Exception {
        rabbitTemplate = Mockito.mock(RabbitTemplate.class);
        publisher = new NotificationEventPublisher(rabbitTemplate);

        Field exchangeField = NotificationEventPublisher.class.getDeclaredField("exchange");
        exchangeField.setAccessible(true);
        exchangeField.set(publisher, "test-exchange");

        Field routingKeyField = NotificationEventPublisher.class.getDeclaredField("routingKey");
        routingKeyField.setAccessible(true);
        routingKeyField.set(publisher, "test-routing-key");
    }

    @Test
    void shouldPublishEvent() {
        NotificationEvent event = new NotificationEvent();

        publisher.publish(event);

        verify(rabbitTemplate).convertAndSend("test-exchange", "test-routing-key", event);
    }
}
