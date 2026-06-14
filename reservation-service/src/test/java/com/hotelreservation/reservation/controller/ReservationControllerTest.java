package com.hotelreservation.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotelreservation.reservation.dto.ReservationRequest;
import com.hotelreservation.reservation.dto.ReservationResponse;
import com.hotelreservation.reservation.exception.ReservationNotFoundException;
import com.hotelreservation.reservation.model.ReservationStatus;
import com.hotelreservation.reservation.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    private ObjectMapper objectMapper;
    private ReservationResponse reservationResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        reservationResponse = ReservationResponse.builder()
                .id(1L)
                .reservationNumber("RES-ABCD1234")
                .userId(1L)
                .roomId(1L)
                .hotelId(1L)
                .guestName("John Doe")
                .guestEmail("john@example.com")
                .checkInDate(LocalDate.now().plusDays(5))
                .checkOutDate(LocalDate.now().plusDays(8))
                .totalPrice(new BigDecimal("360.00"))
                .status(ReservationStatus.CONFIRMED)
                .build();
    }

    @Test
    void createReservation_Returns201() throws Exception {
        when(reservationService.createReservation(any(ReservationRequest.class))).thenReturn(reservationResponse);

        ReservationRequest request = ReservationRequest.builder()
                .userId(1L).roomId(1L)
                .guestName("John Doe").guestEmail("john@example.com")
                .checkInDate(LocalDate.now().plusDays(5))
                .checkOutDate(LocalDate.now().plusDays(8))
                .build();

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reservationNumber").value("RES-ABCD1234"))
                .andExpect(jsonPath("$.guestName").value("John Doe"));
    }

    @Test
    void getReservationById_Returns200() throws Exception {
        when(reservationService.getReservationById(1L)).thenReturn(reservationResponse);

        mockMvc.perform(get("/api/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void getReservationById_NotFound_Returns404() throws Exception {
        when(reservationService.getReservationById(99L))
                .thenThrow(new ReservationNotFoundException(99L));

        mockMvc.perform(get("/api/reservations/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllReservations_Returns200() throws Exception {
        when(reservationService.getAllReservations()).thenReturn(List.of(reservationResponse));

        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reservationNumber").value("RES-ABCD1234"));
    }

    @Test
    void getReservationsByUser_Returns200() throws Exception {
        when(reservationService.getReservationsByUser(1L)).thenReturn(List.of(reservationResponse));

        mockMvc.perform(get("/api/reservations/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    void cancelReservation_Returns200() throws Exception {
        reservationResponse.setStatus(ReservationStatus.CANCELLED);
        when(reservationService.cancelReservation(eq(1L))).thenReturn(reservationResponse);

        mockMvc.perform(patch("/api/reservations/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
