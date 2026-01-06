package com.app.booking.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.app.booking.dto.notification.BookingEventPublisher;
import com.app.booking.dto.notification.NotificationEvent;
import com.app.booking.dto.request.CreateBookingRequest;
import com.app.booking.dto.request.RescheduleBookingRequest;
import com.app.booking.dto.response.BookingDetailsResponse;
import com.app.booking.dto.response.BookingListResponse;
import com.app.booking.dto.response.BookingResponse;
import com.app.booking.dto.response.CancelBookingResponse;
import com.app.booking.dto.response.RescheduleBookingResponse;
import com.app.booking.exception.BookingNotFoundException;
import com.app.booking.exception.BusinessValidationException;
import com.app.booking.model.Address;
import com.app.booking.model.Booking;
import com.app.booking.model.BookingStatus;
import com.app.booking.model.PaymentMode;
import com.app.booking.model.TimeSlot;
import com.app.booking.repository.BookingRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingEventPublisher bookingEventPublisher;

    @InjectMocks
    private BookingService bookingService;

    private Booking booking;
    private CreateBookingRequest createRequest;
    private final String CUSTOMER_ID = "cust-123";
    private final String BOOKING_ID = "BK-12345678";

    @BeforeEach
    void setUp() {
        // Setup a standard valid booking for testing
        booking = Booking.builder()
                .id("mongo-id")
                .bookingId(BOOKING_ID)
                .customerId(CUSTOMER_ID)
                .serviceId("srv-001")
                .serviceName("AC Repair")
                .categoryId("cat-001")
                .categoryName("Appliance")
                .scheduledDate(LocalDate.now().plusDays(5)) // Future date
                .timeSlot(TimeSlot.SLOT_9_11) // Assuming Enum exists as per your screenshot
                .serviceAddress(new Address())
                .status(BookingStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .build();

        createRequest = new CreateBookingRequest();
        createRequest.setServiceId("srv-001");
        createRequest.setServiceName("AC Repair");
        createRequest.setCategoryId("cat-001");
        createRequest.setCategoryName("Appliance");
        createRequest.setScheduledDate(LocalDate.now().plusDays(2));
        createRequest.setTimeSlot(TimeSlot.SLOT_14_16);
        createRequest.setAddress(new Address());
        createRequest.setPaymentMode(PaymentMode.CASH);
    }

    // ==========================================
    // Create Booking Tests
    // ==========================================

    @Test
    void createBooking_Success() {
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking saved = invocation.getArgument(0);
            saved.setBookingId(BOOKING_ID); // Simulate ID generation
            return saved;
        });

        BookingResponse response = bookingService.createBooking(createRequest, CUSTOMER_ID);

        assertNotNull(response);
        assertEquals(BookingStatus.CONFIRMED.name(), response.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(bookingEventPublisher, times(1)).publish(any(NotificationEvent.class));
    }

    // ==========================================
    // Get Booking Tests
    // ==========================================

    @Test
    void getBookingByBookingId_Success() {
        when(bookingRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.of(booking));

        BookingDetailsResponse response = bookingService.getBookingByBookingId(BOOKING_ID);

        assertNotNull(response);
        assertEquals(BOOKING_ID, response.getBookingId());
        assertEquals("AC Repair", response.getServiceName());
    }

    @Test
    void getBookingByBookingId_NotFound() {
        when(bookingRepository.findByBookingId("invalid-id")).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> 
            bookingService.getBookingByBookingId("invalid-id")
        );
    }

    @Test
    void getAllBookings_Success() {
        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        List<BookingListResponse> responses = bookingService.getAllBookings();

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals(BOOKING_ID, responses.get(0).getBookingId());
    }

    @Test
    void getMyBookings_Success() {
        when(bookingRepository.findByCustomerId(CUSTOMER_ID)).thenReturn(List.of(booking));

        List<BookingListResponse> responses = bookingService.getMyBookings(CUSTOMER_ID);

        assertFalse(responses.isEmpty());
        assertEquals(BOOKING_ID, responses.get(0).getBookingId());
    }
    
    @Test
    void getBookingHistory_Success() {
        when(bookingRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(booking));
        
        List<BookingListResponse> responses = bookingService.getBookingHistory();
        
        assertFalse(responses.isEmpty());
        assertEquals(BOOKING_ID, responses.get(0).getBookingId());
    }

    // ==========================================
    // Reschedule Booking Tests
    // ==========================================

    @Test
    void rescheduleBooking_Success() {
        // Arrange
        RescheduleBookingRequest request = new RescheduleBookingRequest();
        // Valid reschedule: Tomorrow + 1 day (within 1-3 day window)
        request.setScheduledDate(LocalDate.now().plusDays(2)); 
        request.setTimeSlot(TimeSlot.SLOT_14_16);

        when(bookingRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.of(booking));

        // Act
        RescheduleBookingResponse response = bookingService.rescheduleBooking(BOOKING_ID, CUSTOMER_ID, request);

        // Assert
        assertEquals("RESCHEDULED", response.getStatus());
        assertEquals(BookingStatus.RESCHEDULED, booking.getStatus());
        assertEquals(request.getScheduledDate(), booking.getScheduledDate());
        verify(bookingRepository).save(booking);
        verify(bookingEventPublisher).publish(any(NotificationEvent.class));
    }

    @Test
    void rescheduleBooking_Unauthorized() {
        when(bookingRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.of(booking));

        RescheduleBookingRequest request = new RescheduleBookingRequest();

        assertThrows(AccessDeniedException.class, () -> 
            bookingService.rescheduleBooking(BOOKING_ID, "wrong-customer-id", request)
        );
    }

    @Test
    void rescheduleBooking_InvalidStatus_Cancelled() {
        booking.setStatus(BookingStatus.CANCELLED);
        when(bookingRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.of(booking));
        
        RescheduleBookingRequest request = new RescheduleBookingRequest();
        
        assertThrows(BusinessValidationException.class, () -> 
            bookingService.rescheduleBooking(BOOKING_ID, CUSTOMER_ID, request)
        );
    }
    
    @Test
    void rescheduleBooking_InvalidStatus_InProgress() {
        booking.setStatus(BookingStatus.IN_PROGRESS);
        when(bookingRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.of(booking));
        
        RescheduleBookingRequest request = new RescheduleBookingRequest();
        
        assertThrows(BusinessValidationException.class, () -> 
            bookingService.rescheduleBooking(BOOKING_ID, CUSTOMER_ID, request)
        );
    }

    @Test
    void rescheduleBooking_Within24Hours_Fail() {
        // Mock current booking is TODAY (implying < 24 hours from now)
        booking.setScheduledDate(LocalDate.now());
        // Depending on actual time of test run, this ensures it's "soon"
        // But cleaner way: Just set scheduled date to today
        
        when(bookingRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.of(booking));
        
        RescheduleBookingRequest request = new RescheduleBookingRequest();

        BusinessValidationException ex = assertThrows(BusinessValidationException.class, () -> 
            bookingService.rescheduleBooking(BOOKING_ID, CUSTOMER_ID, request)
        );
        assertEquals("Booking cannot be rescheduled within 24 hours of service time", ex.getMessage());
    }

    @Test
    void rescheduleBooking_NewDateInPast_Fail() {
        // Current booking is far in future (valid to attempt reschedule)
        booking.setScheduledDate(LocalDate.now().plusDays(10));
        
        when(bookingRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.of(booking));
        
        RescheduleBookingRequest request = new RescheduleBookingRequest();
        request.setScheduledDate(LocalDate.now().minusDays(1)); // Past
        request.setTimeSlot(TimeSlot.SLOT_9_11);

        BusinessValidationException ex = assertThrows(BusinessValidationException.class, () -> 
            bookingService.rescheduleBooking(BOOKING_ID, CUSTOMER_ID, request)
        );
        assertEquals("Cannot reschedule to past date or time", ex.getMessage());
    }

    @Test
    void rescheduleBooking_WindowViolation_TooFar() {
        // Current booking valid
        booking.setScheduledDate(LocalDate.now().plusDays(10));
        
        when(bookingRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.of(booking));
        
        RescheduleBookingRequest request = new RescheduleBookingRequest();
        request.setScheduledDate(LocalDate.now().plusDays(5)); // Violation: > today + 3
        request.setTimeSlot(TimeSlot.SLOT_9_11);

        BusinessValidationException ex = assertThrows(BusinessValidationException.class, () -> 
            bookingService.rescheduleBooking(BOOKING_ID, CUSTOMER_ID, request)
        );
        assertTrue(ex.getMessage().contains("Rescheduled date must be between tomorrow and the next 3 days"));
    }

    // ==========================================
    // Cancel Booking Tests
    // ==========================================

    @Test
    void cancelBooking_Success() {
        // Booking is 5 days away (safe to cancel)
        when(bookingRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.of(booking));

        CancelBookingResponse response = bookingService.cancelBooking(BOOKING_ID, CUSTOMER_ID);

        assertEquals("CANCELLED", response.getStatus());
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        verify(bookingRepository).save(booking);
        verify(bookingEventPublisher).publish(any(NotificationEvent.class));
    }

    @Test
    void cancelBooking_Unauthorized() {
        when(bookingRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.of(booking));

        assertThrows(AccessDeniedException.class, () -> 
            bookingService.cancelBooking(BOOKING_ID, "stranger-id")
        );
    }

    @Test
    void cancelBooking_AlreadyCompleted_Fail() {
        booking.setStatus(BookingStatus.COMPLETED);
        when(bookingRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.of(booking));

        assertThrows(BusinessValidationException.class, () -> 
            bookingService.cancelBooking(BOOKING_ID, CUSTOMER_ID)
        );
    }
    
    @Test
    void cancelBooking_InProgress_Fail() {
        booking.setStatus(BookingStatus.IN_PROGRESS);
        when(bookingRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.of(booking));

        assertThrows(BusinessValidationException.class, () -> 
            bookingService.cancelBooking(BOOKING_ID, CUSTOMER_ID)
        );
    }

    @Test
    void cancelBooking_Within24Hours_Fail() {
        // Set booking to be happening right now or today
        booking.setScheduledDate(LocalDate.now());
        booking.setTimeSlot(TimeSlot.SLOT_9_11);
        
        // Even if we run this test at 8 AM and slot is 9 AM, it is < 24 hours.
        when(bookingRepository.findByBookingId(BOOKING_ID)).thenReturn(Optional.of(booking));

        BusinessValidationException ex = assertThrows(BusinessValidationException.class, () -> 
            bookingService.cancelBooking(BOOKING_ID, CUSTOMER_ID)
        );
        assertEquals("Booking cannot be cancelled within 24 hours of service time", ex.getMessage());
    }
}