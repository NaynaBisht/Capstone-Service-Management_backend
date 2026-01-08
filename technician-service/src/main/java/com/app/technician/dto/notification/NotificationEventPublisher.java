package com.app.technician.dto.notification;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${notification.exchange}")
    private String exchange;

    @Value("${notification.routing-key}")
    private String routingKey;

    public void publish(NotificationEvent event) {
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}

