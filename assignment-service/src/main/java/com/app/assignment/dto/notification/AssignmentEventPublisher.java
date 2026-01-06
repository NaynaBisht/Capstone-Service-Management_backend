package com.app.assignment.dto.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssignmentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${notification.exchange}")
    private String exchange;

    public void publish(NotificationEvent event) {
        rabbitTemplate.convertAndSend(exchange, "notification.event", event);

        log.info(
            "Published assignment notification event [{}] for user [{}]",
            event.getEventType(),
            event.getRecipient().getUserId()
        );
    }
}
