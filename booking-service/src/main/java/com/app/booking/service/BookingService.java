package com.app.booking.service;

import java.time.Duration;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.app.booking.dto.request.CreateBookingRequest;
import com.app.booking.dto.request.RescheduleBookingRequest;
import com.app.booking.dto.response.BookingDetailsResponse;
import com.app.booking.dto.response.BookingListResponse;
import com.app.booking.dto.response.BookingResponse;
import com.app.booking.dto.response.CancelBookingResponse;
import com.app.booking.dto.response.RescheduleBookingResponse;
import com.app.booking.exception.BookingNotFoundException;
import com.app.booking.exception.BusinessValidationException;
import com.app.booking.model.Booking;
import com.app.booking.model.BookingStatus;
import com.app.booking.repository.BookingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

        private final BookingRepository bookingRepository;

        public BookingResponse createBooking(CreateBookingRequest request, String customerId) {

                Booking booking = Booking.builder()
                                .bookingId("BK-" + UUID.randomUUID().toString().substring(0, 8))
                                .customerId(customerId)
                                .serviceName(request.getServiceName())
                                .categoryName(request.getCategoryName())
                                .scheduledDate(request.getScheduledDate())
                                .timeSlot(request.getTimeSlot())
                                .address(request.getAddress())
                                .issueDescription(request.getIssueDescription())
                                .paymentMode(request.getPaymentMode())
                                .status(BookingStatus.CONFIRMED)
                                .createdAt(LocalDateTime.now())
                                .build();

                bookingRepository.save(booking);

                return BookingResponse.builder()
                                .bookingId(booking.getBookingId())
                                .status(booking.getStatus().name())
                                .build();
        }

        public List<BookingListResponse> getAllBookings() {

                List<Booking> bookings = bookingRepository.findAll();

                return bookings.stream()
                                .map(booking -> BookingListResponse.builder()
                                                .bookingId(booking.getBookingId())
                                                .customerId(booking.getCustomerId())
                                                .serviceName(booking.getServiceName())
                                                .categoryName(booking.getCategoryName())
                                                .scheduledDate(booking.getScheduledDate())
                                                .timeSlot(booking.getTimeSlot())
                                                .address(booking.getAddress())
                                                .status(booking.getStatus().name())
                                                .createdAt(booking.getCreatedAt())
                                                .build())
                                .toList();
        }

        public BookingDetailsResponse getBookingByBookingId(String bookingId) {

                Booking booking = bookingRepository.findByBookingId(bookingId)
                                .orElseThrow(() -> new NoSuchElementException(
                                                "Booking not found with bookingId: " + bookingId));

                return BookingDetailsResponse.builder()
                                .bookingId(booking.getBookingId())
                                .customerId(booking.getCustomerId())
                                .serviceName(booking.getServiceName())
                                .categoryName(booking.getCategoryName())
                                .scheduledDate(booking.getScheduledDate())
                                .timeSlot(booking.getTimeSlot())
                                .address(booking.getAddress())
                                .issueDescription(booking.getIssueDescription())
                                .paymentMode(booking.getPaymentMode())
                                .status(booking.getStatus().name())
                                .createdAt(booking.getCreatedAt())
                                .build();
        }
}
