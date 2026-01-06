package com.app.booking.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
// CORRECT IMPORTS:
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.app.booking.dto.response.BookingDetailsResponse;
import com.app.booking.service.BookingService;
import com.app.booking.util.JwtUtil; 

@WebMvcTest(InternalBookingController.class)
@AutoConfigureMockMvc(addFilters = false)
class InternalBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void getBooking_Success() throws Exception {
        String bookingId = "BK-INT-1";
        BookingDetailsResponse response = BookingDetailsResponse.builder()
                .bookingId(bookingId)
                .serviceName("Internal Check")
                .build();

        given(bookingService.getBookingByBookingId(bookingId)).willReturn(response);

        mockMvc.perform(get("/internal/bookings/{bookingId}", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(bookingId))
                .andExpect(jsonPath("$.serviceName").value("Internal Check"));
    }
}