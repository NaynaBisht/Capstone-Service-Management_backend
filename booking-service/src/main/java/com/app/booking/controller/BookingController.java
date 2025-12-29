package com.app.booking.controller;

import com.app.booking.dto.request.CreateBookingRequest;
import com.app.booking.dto.request.RescheduleBookingRequest;
import com.app.booking.dto.response.BookingDetailsResponse;
import com.app.booking.dto.response.BookingListResponse;
import com.app.booking.dto.response.BookingResponse;
import com.app.booking.dto.response.CancelBookingResponse;
import com.app.booking.dto.response.RescheduleBookingResponse;
import com.app.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

	private final BookingService bookingService;

	@PostMapping
	public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
		String customerId = "CUSTOMER_001";

		BookingResponse response = bookingService.createBooking(request, customerId);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<BookingListResponse>> getAllBookings() {
		List<BookingListResponse> bookings = bookingService.getAllBookings();
		return ResponseEntity.ok(bookings);
	}

	@GetMapping("{bookingId}")
	public ResponseEntity<BookingDetailsResponse> getBookingByBookingId(@PathVariable String bookingId) {

		BookingDetailsResponse response = bookingService.getBookingByBookingId(bookingId);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/my-bookings")
	public ResponseEntity<List<BookingListResponse>> getMyBookings() {

		// TEMP: until JWT is added
		String customerId = "CUSTOMER_001";

		List<BookingListResponse> bookings = bookingService.getMyBookings(customerId);

		return ResponseEntity.ok(bookings);
	}

	@GetMapping("/history")
	public ResponseEntity<List<BookingListResponse>> getBookingHistory() {

		List<BookingListResponse> history = bookingService.getBookingHistory();

		return ResponseEntity.ok(history);
	}
}
