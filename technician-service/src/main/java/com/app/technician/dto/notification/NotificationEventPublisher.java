package com.app.technician.dto.notification;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${notification.exchange}")
    private String exchange;

    @Value("${notification.routing-key}")
    private String routingKey;

    public void publish(NotificationEvent event) {
        // ADD THESE LOGS TO SEE IF THIS RUNS
        log.info("Attempting to publish event: {} to exchange: {} with key: {}", 
                 event.getEventType(), exchange, routingKey);
        
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            log.info("Successfully published event to RabbitMQ");
        } catch (Exception e) {
            log.error("FAILED to publish to RabbitMQ: {}", e.getMessage(), e);
        }
    }
}

