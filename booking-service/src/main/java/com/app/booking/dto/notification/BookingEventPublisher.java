package com.app.booking.dto.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE = "notification.exchange";
    private static final String ROUTING_KEY = "notification.event";

    public void publish(NotificationEvent event) {

        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);

        log.info(
            "Published booking event [{}]",
            event.getEventType()
        );
    }
}
