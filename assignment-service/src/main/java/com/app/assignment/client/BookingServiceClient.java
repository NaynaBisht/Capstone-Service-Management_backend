package com.app.assignment.client;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BookingServiceClient {

	private final RestClient restClient;

	@Value("${booking.service.url}")
	private String bookingServiceUrl;

	// Constructor: Manually creating the client
	public BookingServiceClient() {
		this.restClient = RestClient.create();
	}

	public void validateBooking(String bookingId, String token) {
		restClient.get().uri(bookingServiceUrl + "/internal/bookings/{id}", bookingId)
				.header("Authorization", "Bearer " + token).retrieve().toBodilessEntity();
	}

	public BookingDTO getBooking(String bookingId, String token) {
		return restClient.get().uri(bookingServiceUrl + "/internal/bookings/{id}", bookingId)
				.header("Authorization", "Bearer " + token).retrieve().body(BookingDTO.class);
	}

	@Data
	public static class BookingDTO {
		private String bookingId;
		private String customerId;
		private String customerEmail;
		private String serviceName;
	}
}