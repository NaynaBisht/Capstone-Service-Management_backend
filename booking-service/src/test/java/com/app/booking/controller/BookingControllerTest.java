package com.app.booking.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.app.booking.dto.request.CreateBookingRequest;
import com.app.booking.dto.request.RescheduleBookingRequest;
import com.app.booking.dto.response.BookingDetailsResponse;
import com.app.booking.dto.response.BookingListResponse;
import com.app.booking.dto.response.BookingResponse;
import com.app.booking.dto.response.CancelBookingResponse;
import com.app.booking.dto.response.RescheduleBookingResponse;
import com.app.booking.model.Address;
import com.app.booking.model.PaymentMode;
import com.app.booking.model.TimeSlot;
import com.app.booking.service.BookingService;
import com.app.booking.util.JwtUtil; // Import your JwtUtil
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false) 
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private final String CUSTOMER_ID = "cust-123";

    @BeforeEach
    void setUp() {
        // 1. Mock the Authentication object
        Authentication authentication = Mockito.mock(Authentication.class);
        // 2. Mock the SecurityContext
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        // 3. Configure them to return your specific Customer ID String
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(CUSTOMER_ID);

        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createBooking_Success() throws Exception {
        CreateBookingRequest request = CreateBookingRequest.builder()
                .serviceId("srv-01")
                .serviceName("Cleaning")
                .categoryId("cat-01")
                .categoryName("Home")
                .scheduledDate(LocalDate.now().plusDays(1)) 
                .timeSlot(TimeSlot.SLOT_9_11) 
                .address(new Address()) 
                .issueDescription("Dusty")
                .paymentMode(PaymentMode.CASH)
                .build();

        BookingResponse response = BookingResponse.builder()
                .bookingId("BK-123")
                .status("CONFIRMED")
                .build();

        given(bookingService.createBooking(any(CreateBookingRequest.class), eq(CUSTOMER_ID)))
                .willReturn(response);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value("BK-123"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void createBooking_ValidationFailure() throws Exception {
        CreateBookingRequest invalidRequest = new CreateBookingRequest(); 

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()); 
    }

    @Test
    void getAllBookings_Success() throws Exception {
        BookingListResponse listResponse = BookingListResponse.builder()
                .bookingId("BK-123")
                .build();

        given(bookingService.getAllBookings()).willReturn(List.of(listResponse));

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].bookingId").value("BK-123"));
    }

    @Test
    void getBookingByBookingId_Success() throws Exception {
        BookingDetailsResponse response = BookingDetailsResponse.builder()
                .bookingId("BK-123")
                .serviceName("Repair")
                .build();

        given(bookingService.getBookingByBookingId("BK-123")).willReturn(response);

        mockMvc.perform(get("/api/bookings/{bookingId}", "BK-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value("BK-123"))
                .andExpect(jsonPath("$.serviceName").value("Repair"));
    }

    @Test
    void getMyBookings_Success() throws Exception {
        BookingListResponse listResponse = BookingListResponse.builder()
                .bookingId("BK-MY-1")
                .build();

        given(bookingService.getMyBookings(CUSTOMER_ID)).willReturn(List.of(listResponse));

        mockMvc.perform(get("/api/bookings/my-bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getBookingHistory_Success() throws Exception {
        given(bookingService.getBookingHistory()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/bookings/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void rescheduleBooking_Success() throws Exception {
        RescheduleBookingRequest request = RescheduleBookingRequest.builder()
                .scheduledDate(LocalDate.now().plusDays(2))
                .timeSlot(TimeSlot.SLOT_14_16)
                .build();

        RescheduleBookingResponse response = RescheduleBookingResponse.builder()
                .bookingId("BK-123")
                .status("RESCHEDULED")
                .message("Done")
                .build();

        given(bookingService.rescheduleBooking(eq("BK-123"), eq(CUSTOMER_ID), any(RescheduleBookingRequest.class)))
                .willReturn(response);

        mockMvc.perform(put("/api/bookings/{bookingId}/reschedule", "BK-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESCHEDULED"));
    }

    @Test
    void cancelBooking_Success() throws Exception {
        CancelBookingResponse response = CancelBookingResponse.builder()
                .bookingId("BK-123")
                .status("CANCELLED")
                .build();

        given(bookingService.cancelBooking("BK-123", CUSTOMER_ID)).willReturn(response);

        mockMvc.perform(put("/api/bookings/{bookingId}/cancel", "BK-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}