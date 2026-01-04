package com.app.booking.controller;

import com.app.booking.dto.response.BookingDetailsResponse;
import com.app.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/bookings")
@RequiredArgsConstructor
public class InternalBookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDetailsResponse> getBooking(@PathVariable String bookingId) {
        // Reusing the existing service method
        BookingDetailsResponse response = bookingService.getBookingByBookingId(bookingId);
        return ResponseEntity.ok(response);
    }
}
