package com.app.booking.consumer;

import com.app.booking.dto.notification.NotificationEvent;
import com.app.booking.dto.notification.NotificationEventType;
import com.app.booking.model.Booking;
import com.app.booking.model.BookingStatus;
import com.app.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssignmentStatusListener {

    private final BookingRepository bookingRepository;

    @RabbitListener(queues = "${notification.queue}")
    public void handleAssignmentStatusUpdate(NotificationEvent event) {
        log.info("Received event: {} at {}", event.getEventType(), event.getTimestamp());

        String bookingId = (String) event.getData().get("bookingId");

        if (event.getEventType() == NotificationEventType.ASSIGNMENT_COMPLETED) {
            updateBookingStatus(bookingId, BookingStatus.COMPLETED);
        } else if (event.getEventType() == NotificationEventType.ASSIGNMENT_STARTED) {
            updateBookingStatus(bookingId, BookingStatus.IN_PROGRESS);
        }
    }

    private void updateBookingStatus(String bookingId, BookingStatus status) {
        bookingRepository.findByBookingId(bookingId).ifPresentOrElse(booking -> {
            booking.setStatus(status);
            bookingRepository.save(booking);
            log.info("Successfully updated Booking ID: {} to status: {}", bookingId, status);
        }, () -> log.error("Booking ID: {} not found in database", bookingId));
    }
}