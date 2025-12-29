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
        public List<BookingListResponse> getMyBookings(String customerId) {

            List<Booking> bookings =
                    bookingRepository.findByCustomerId(customerId);

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
                            .build()
                    )
                    .toList();
        }
        public List<BookingListResponse> getBookingHistory() {

            List<Booking> bookings =
                    bookingRepository.findAllByOrderByCreatedAtDesc();

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
                            .build()
                    )
                    .toList();
        }

        public RescheduleBookingResponse rescheduleBooking(String bookingId, RescheduleBookingRequest request) {

            Booking booking = bookingRepository.findByBookingId(bookingId)
                    .orElseThrow(() ->
                            new BookingNotFoundException(bookingId)
                    );

            // check if booking is cancelled
            if (booking.getStatus() == BookingStatus.CANCELLED) {
            	throw new BusinessValidationException(
                        "Cancelled booking cannot be rescheduled");
//                return RescheduleBookingResponse.builder()
//                        .bookingId(bookingId)
//                        .status("CANCELLED")
//                        .message("Cancelled booking cannot be rescheduled")
//                        .build();
            }

            LocalTime newStartTime = parseStartTime(request.getTimeSlot());
            LocalDateTime newDateTime =
                    LocalDateTime.of(request.getScheduledDate(), newStartTime);

            if (newDateTime.isBefore(LocalDateTime.now())) {
                throw new BusinessValidationException(
                        "Cannot reschedule to past date or time");
            }
            
            booking.setScheduledDate(request.getScheduledDate());
            booking.setTimeSlot(request.getTimeSlot());
            
            bookingRepository.save(booking);

            return RescheduleBookingResponse.builder()
                    .bookingId(bookingId)
                    .status(booking.getStatus().name())
                    .message("Booking has been rescheduled")
                    .build();
        }

        public CancelBookingResponse cancelBooking(String bookingId) {

            Booking booking = bookingRepository.findByBookingId(bookingId)
                    .orElseThrow(() ->
                            new BookingNotFoundException(bookingId)
                    );

//            check is booking already cancelled
            if (booking.getStatus() == BookingStatus.CANCELLED) {
            	throw new BusinessValidationException("Booking already cancelled");
//                return CancelBookingResponse.builder()
//                        .bookingId(bookingId)
//                        .status("CANCELLED")
//                        .message("Booking is already cancelled")
//                        .build();
            }
            
            LocalTime startTime = parseStartTime(booking.getTimeSlot());
            LocalDateTime bookingDateTime =
                    LocalDateTime.of(booking.getScheduledDate(), startTime);

            if (Duration.between(LocalDateTime.now(), bookingDateTime).toHours() < 24) {
                throw new BusinessValidationException(
                        "Booking cannot be cancelled within 24 hours of service time");
            }

            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            return CancelBookingResponse.builder()
                    .bookingId(bookingId)
                    .status("CANCELLED")
                    .message("Booking cancelled")
                    .build();
        }

        private LocalTime parseStartTime(String timeSlot) {
			try {
				String start = timeSlot.split("-")[0].trim(); // "10:00"
				return LocalTime.parse(start);
			} catch (Exception ex) {
				throw new BusinessValidationException("Invalid time slot format. Expected format: HH:mm - HH:mm");
			}
		}

		private LocalTime parseEndTime(String timeSlot) {
			try {
				String end = timeSlot.split("-")[1].trim(); // "12:00"
				return LocalTime.parse(end);
			} catch (Exception ex) {
				throw new BusinessValidationException("Invalid time slot format. Expected format: HH:mm - HH:mm");
			}
		}

		public List<BookingListResponse> getAssignedBookingsForTechnician(
                String technicianId
        ) {

            List<Booking> bookings =
                    bookingRepository.findByTechnicianId(technicianId);

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
                            .build()
                    )
                    .toList();
        }
}
