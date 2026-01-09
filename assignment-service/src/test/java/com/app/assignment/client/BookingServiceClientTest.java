package com.app.assignment.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class BookingServiceClientTest {

    private BookingServiceClient bookingServiceClient;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        bookingServiceClient = new BookingServiceClient();

        // Create builder (NOT RestClient directly)
        RestClient.Builder builder = RestClient.builder();

        // Bind MockRestServiceServer to the builder
        mockServer = MockRestServiceServer.bindTo(builder).build();

        // Build RestClient from builder
        RestClient restClient = builder.build();

        // Inject bookingServiceUrl
        try {
            var field = BookingServiceClient.class.getDeclaredField("bookingServiceUrl");
            field.setAccessible(true);
            field.set(bookingServiceClient, "http://localhost:8081");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Inject RestClient into BookingServiceClient
        try {
            var field = BookingServiceClient.class.getDeclaredField("restClient");
            field.setAccessible(true);
            field.set(bookingServiceClient, restClient);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void validateBooking_shouldCallBookingService() {
        mockServer.expect(requestTo("http://localhost:8081/internal/bookings/123"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer test-token"))
                .andRespond(withSuccess());

        bookingServiceClient.validateBooking("123", "test-token");

        mockServer.verify();
    }

    @Test
    void getBooking_shouldReturnBookingDTO() {
        String responseJson = """
                {
                  "bookingId": "123",
                  "customerId": "C001",
                  "customerEmail": "test@example.com",
                  "serviceName": "Plumbing",
                  "categoryId": "CAT01",
                  "categoryName": "Home Services"
                }
                """;

        mockServer.expect(requestTo("http://localhost:8081/internal/bookings/123"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer test-token"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        BookingServiceClient.BookingDTO booking =
                bookingServiceClient.getBooking("123", "test-token");

        mockServer.verify();

        assertEquals("123", booking.getBookingId());
        assertEquals("C001", booking.getCustomerId());
        assertEquals("test@example.com", booking.getCustomerEmail());
        assertEquals("Plumbing", booking.getServiceName());
        assertEquals("CAT01", booking.getCategoryId());
        assertEquals("Home Services", booking.getCategoryName());
    }
}
