package com.app.booking.consumer;

import com.app.booking.dto.notification.NotificationEvent;
import com.app.booking.dto.notification.NotificationEventType;
import com.app.booking.model.Booking;
import com.app.booking.model.BookingStatus;
import com.app.booking.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

class AssignmentStatusListenerTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private AssignmentStatusListener listener;

    private Booking booking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        booking = new Booking();
        booking.setBookingId("BOOK123");
        booking.setStatus(BookingStatus.IN_PROGRESS);
    }

    @Test
    void handleAssignmentStatusUpdate_shouldUpdateStatusToCompleted() {
        // Arrange
        Map<String, Object> data = new HashMap<>();
        data.put("bookingId", "BOOK123");

        NotificationEvent event = NotificationEvent.builder()
                .eventType(NotificationEventType.ASSIGNMENT_COMPLETED)
                .timestamp(Instant.now())
                .data(data)
                .build();

        when(bookingRepository.findByBookingId("BOOK123"))
                .thenReturn(Optional.of(booking));

        // Act
        listener.handleAssignmentStatusUpdate(event);

        // Assert
        verify(bookingRepository, times(1)).findByBookingId("BOOK123");
        verify(bookingRepository, times(1)).save(booking);
        assert booking.getStatus() == BookingStatus.COMPLETED;
    }

    @Test
    void handleAssignmentStatusUpdate_shouldUpdateStatusToInProgress() {
        // Arrange
        Map<String, Object> data = new HashMap<>();
        data.put("bookingId", "BOOK123");

        NotificationEvent event = NotificationEvent.builder()
                .eventType(NotificationEventType.ASSIGNMENT_STARTED)
                .timestamp(Instant.now())
                .data(data)
                .build();

        when(bookingRepository.findByBookingId("BOOK123"))
                .thenReturn(Optional.of(booking));

        // Act
        listener.handleAssignmentStatusUpdate(event);

        // Assert
        verify(bookingRepository, times(1)).findByBookingId("BOOK123");
        verify(bookingRepository, times(1)).save(booking);
        assert booking.getStatus() == BookingStatus.IN_PROGRESS;
    }

    @Test
    void handleAssignmentStatusUpdate_shouldNotSaveWhenBookingNotFound() {
        // Arrange
        Map<String, Object> data = new HashMap<>();
        data.put("bookingId", "INVALID_ID");

        NotificationEvent event = NotificationEvent.builder()
                .eventType(NotificationEventType.ASSIGNMENT_COMPLETED)
                .timestamp(Instant.now())
                .data(data)
                .build();

        when(bookingRepository.findByBookingId("INVALID_ID"))
                .thenReturn(Optional.empty());

        // Act
        listener.handleAssignmentStatusUpdate(event);

        // Assert
        verify(bookingRepository, times(1)).findByBookingId("INVALID_ID");
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void handleAssignmentStatusUpdate_shouldIgnoreOtherEventTypes() {
        // Arrange
        Map<String, Object> data = new HashMap<>();
        data.put("bookingId", "BOOK123");

        NotificationEvent event = NotificationEvent.builder()
                .eventType(NotificationEventType.BOOKING_CREATED) // some other event
                .timestamp(Instant.now())
                .data(data)
                .build();

        // Act
        listener.handleAssignmentStatusUpdate(event);

        // Assert
        verify(bookingRepository, never()).findByBookingId(any());
        verify(bookingRepository, never()).save(any());
    }
}
