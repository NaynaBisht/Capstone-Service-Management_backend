package com.app.booking.service;

import java.time.Duration;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.AmqpException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.app.booking.dto.notification.BookingEventPublisher;
import com.app.booking.dto.notification.NotificationEvent;
import com.app.booking.dto.notification.NotificationEventType;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

        private final BookingRepository bookingRepository;
        private final BookingEventPublisher bookingEventPublisher;

        private void validateBookingOwnership(Booking booking, String customerId) {
                if (!booking.getCustomerId().equals(customerId)) {
                        throw new AccessDeniedException("Unauthorized access to booking");
                }
        }

        public BookingResponse createBooking(CreateBookingRequest request, String customerId) {

                Booking booking = Booking.builder()
                                .bookingId("BK-" + UUID.randomUUID().toString().substring(0, 8))
                                .customerId(customerId)

                                .serviceId(request.getServiceId())
                                .serviceName(request.getServiceName())
                                .categoryId(request.getCategoryId())
                                .categoryName(request.getCategoryName())

                                .scheduledDate(request.getScheduledDate())
                                .timeSlot(request.getTimeSlot())

                                .serviceAddress(request.getAddress())

                                .issueDescription(request.getIssueDescription())
                                .paymentMode(request.getPaymentMode())

                                .status(BookingStatus.CONFIRMED)
                                .createdAt(LocalDateTime.now())
                                .build();

                bookingRepository.save(booking);
                
                NotificationEvent event = NotificationEvent.builder()
                	    .eventType(NotificationEventType.BOOKING_CREATED)
                	    .recipient(
                	        new NotificationEvent.Recipient(booking.getCustomerId())
                	    )
                	    .data(Map.of(
                	        "bookingId", booking.getBookingId(),
                	        "serviceName", booking.getServiceName(),
                	        "scheduledDate", booking.getScheduledDate(),
                	        "timeSlot", booking.getTimeSlot()
                	    ))
                	    .timestamp(Instant.now())
                	    .build();

                	
                	try {
                		bookingEventPublisher.publish(event);
                	} catch (AmqpException e) {
                        log.warn("RabbitMQ not available. Skipping notification", e);
                    }


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
                                                .serviceId(booking.getServiceId())
                                                .serviceName(booking.getServiceName())
                                                .categoryId(booking.getCategoryId())
                                                .categoryName(booking.getCategoryName())
                                                .scheduledDate(booking.getScheduledDate())
                                                .timeSlot(booking.getTimeSlot())
                                                .serviceAddress(booking.getServiceAddress())
                                                .status(booking.getStatus())
                                                .createdAt(booking.getCreatedAt())
                                                .build())
                                .toList();
        }

        public BookingDetailsResponse getBookingByBookingId(String bookingId) {

                Booking booking = bookingRepository.findByBookingId(bookingId)
                                .orElseThrow(() -> new BookingNotFoundException(bookingId));

                return BookingDetailsResponse.builder()
                                .bookingId(booking.getBookingId())
                                .customerId(booking.getCustomerId())
                                .serviceId(booking.getServiceId())
                                .serviceName(booking.getServiceName())
                                .categoryId(booking.getCategoryId())
                                .categoryName(booking.getCategoryName())
                                .scheduledDate(booking.getScheduledDate())
                                .timeSlot(booking.getTimeSlot())
                                .serviceAddress(booking.getServiceAddress())
                                .issueDescription(booking.getIssueDescription())
                                .paymentMode(booking.getPaymentMode())
                                .status(booking.getStatus())
                                .createdAt(booking.getCreatedAt())
                                .build();
        }

        public List<BookingListResponse> getMyBookings(String customerId) {

                List<Booking> bookings = bookingRepository.findByCustomerId(customerId);

                return bookings.stream()
                                .map(booking -> BookingListResponse.builder()
                                                .bookingId(booking.getBookingId())
                                                // customerId intentionally omitted
                                                .serviceId(booking.getServiceId())
                                                .serviceName(booking.getServiceName())
                                                .categoryId(booking.getCategoryId())
                                                .categoryName(booking.getCategoryName())
                                                .scheduledDate(booking.getScheduledDate())
                                                .timeSlot(booking.getTimeSlot())
                                                .serviceAddress(booking.getServiceAddress())
                                                .status(booking.getStatus())
                                                .createdAt(booking.getCreatedAt())
                                                .build())
                                .toList();
        }

        public List<BookingListResponse> getBookingHistory() {

                List<Booking> bookings = bookingRepository.findAllByOrderByCreatedAtDesc();

                return bookings.stream()
                                .map(booking -> BookingListResponse.builder()
                                                .bookingId(booking.getBookingId())
                                                .customerId(booking.getCustomerId())
                                                .serviceId(booking.getServiceId())
                                                .serviceName(booking.getServiceName())
                                                .categoryId(booking.getCategoryId())
                                                .categoryName(booking.getCategoryName())
                                                .scheduledDate(booking.getScheduledDate())
                                                .timeSlot(booking.getTimeSlot())
                                                .serviceAddress(booking.getServiceAddress())
                                                .status(booking.getStatus())
                                                .createdAt(booking.getCreatedAt())
                                                .build())
                                .toList();
        }

		public RescheduleBookingResponse rescheduleBooking(String bookingId, String customerId,
				RescheduleBookingRequest request) {

			Booking booking = bookingRepository.findByBookingId(bookingId)
					.orElseThrow(() -> new BookingNotFoundException(bookingId));

			validateBookingOwnership(booking, customerId);

			if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.COMPLETED) {
				throw new BusinessValidationException("Booking cannot be rescheduled");
			}

			if (booking.getStatus() == BookingStatus.IN_PROGRESS) {
				throw new BusinessValidationException("Booking already in progress");
			}

			// Existing booking time
			LocalDateTime currentBookingDateTime = LocalDateTime.of(booking.getScheduledDate(),
					booking.getTimeSlot().getStart());

			if (Duration.between(LocalDateTime.now(), currentBookingDateTime).toHours() < 24) {
				throw new BusinessValidationException("Booking cannot be rescheduled within 24 hours of service time");
			}

			// New requested time
			LocalDateTime newDateTime = LocalDateTime.of(request.getScheduledDate(), request.getTimeSlot().getStart());

			if (newDateTime.isBefore(LocalDateTime.now())) {
				throw new BusinessValidationException("Cannot reschedule to past date or time");
			}

			// Enforce booking window: tomorrow to next 3 days
			LocalDate today = LocalDate.now();
			if (request.getScheduledDate().isBefore(today.plusDays(1))
					|| request.getScheduledDate().isAfter(today.plusDays(3))) {
				throw new BusinessValidationException("Rescheduled date must be between tomorrow and the next 3 days");
			}

			booking.setScheduledDate(request.getScheduledDate());
			booking.setTimeSlot(request.getTimeSlot());
			booking.setStatus(BookingStatus.RESCHEDULED);
			bookingRepository.save(booking);

			NotificationEvent event = NotificationEvent.builder().eventType(NotificationEventType.BOOKING_RESCHEDULED)
					.recipient(new NotificationEvent.Recipient(booking.getCustomerId()))
					.data(Map.of("bookingId", booking.getBookingId(), "serviceName", booking.getServiceName(),
							"scheduledDate", booking.getScheduledDate(), "timeSlot", booking.getTimeSlot()))
					.timestamp(Instant.now()).build();

			try {
				bookingEventPublisher.publish(event);
			} catch (AmqpException e) {
                log.warn("RabbitMQ not available. Skipping notification", e);
            }


			return RescheduleBookingResponse.builder().bookingId(bookingId).status("RESCHEDULED")
					.message("Booking has been rescheduled").build();
		}


        public CancelBookingResponse cancelBooking(String bookingId, String customerId) {

            Booking booking = bookingRepository.findByBookingId(bookingId)
                    .orElseThrow(() -> new BookingNotFoundException(bookingId));

            validateBookingOwnership(booking, customerId);

            if (booking.getStatus() == BookingStatus.CANCELLED ||
                booking.getStatus() == BookingStatus.COMPLETED) {
                throw new BusinessValidationException("Booking cannot be cancelled");
            }

            if (booking.getStatus() == BookingStatus.IN_PROGRESS) {
                throw new BusinessValidationException("Booking already in progress");
            }

            LocalDateTime bookingDateTime = LocalDateTime.of(
                    booking.getScheduledDate(),
                    booking.getTimeSlot().getStart());

            if (Duration.between(LocalDateTime.now(), bookingDateTime).toHours() < 24) {
                throw new BusinessValidationException(
                        "Booking cannot be cancelled within 24 hours of service time");
            }

            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            NotificationEvent event = NotificationEvent.builder()
                .eventType(NotificationEventType.BOOKING_CANCELLED)
                .recipient(new NotificationEvent.Recipient(booking.getCustomerId()))
                .data(Map.of(
                    "bookingId", booking.getBookingId(),
                    "serviceName", booking.getServiceName()
                ))
                .timestamp(Instant.now())
                .build();

            try {
                bookingEventPublisher.publish(event);
            } catch (AmqpException e) {
                log.warn("RabbitMQ not available. Skipping notification", e);
            }
            
            return CancelBookingResponse.builder()
                    .bookingId(bookingId)
                    .status("CANCELLED")
                    .message("Booking cancelled")
                    .build();
        }

}
