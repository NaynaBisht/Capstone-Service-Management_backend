package com.app.booking.dto.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${notification.exchange}")
    private String exchange;

    public void publish(String routingKey, NotificationEvent event) {

        rabbitTemplate.convertAndSend(exchange, routingKey, event);

        log.info(
            "Published booking event [{}] with routing key [{}]",
            event.getEventType(),
            routingKey
        );
    }
}


