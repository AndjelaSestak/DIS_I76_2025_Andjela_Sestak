package com.hotelreservation.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotelreservation.reservation.client.HotelClient;
import com.hotelreservation.reservation.dto.ReservationRequest;
import com.hotelreservation.reservation.dto.RoomResponse;
import com.hotelreservation.reservation.model.Reservation;
import com.hotelreservation.reservation.model.ReservationStatus;
import com.hotelreservation.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservationRepository reservationRepository;

    @MockBean
    private HotelClient hotelClient;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    private ReservationRequest testRequest;
    private RoomResponse availableRoom;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();

        objectMapper.registerModule(new JavaTimeModule());

        availableRoom = RoomResponse.builder()
                .id(1L)
                .hotelId(1L)
                .hotelName("Grand Hotel Belgrade")
                .roomNumber("101")
                .type("STANDARD")
                .pricePerNight(new BigDecimal("100.00"))
                .capacity(2)
                .available(true)
                .build();

        testRequest = ReservationRequest.builder()
                .userId(1L)
                .roomId(1L)
                .guestName("Ana Jovic")
                .guestEmail("ana@example.com")
                .checkInDate(LocalDate.now().plusDays(5))
                .checkOutDate(LocalDate.now().plusDays(8))
                .build();
    }

    @AfterEach
    void tearDown() {
        reservationRepository.deleteAll();
    }

    @Test
    void createReservation_Success() throws Exception {
        when(hotelClient.getRoomById(1L)).thenReturn(availableRoom);
        when(hotelClient.updateRoomAvailability(anyLong(), anyBoolean())).thenReturn(availableRoom);
        doNothing().when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.guestName").value("Ana Jovic"))
                .andExpect(jsonPath("$.guestEmail").value("ana@example.com"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.reservationNumber").exists())
                .andExpect(jsonPath("$.totalPrice").value(300.00));

        assertThat(reservationRepository.count()).isEqualTo(1);
    }

    @Test
    void createReservation_RoomNotAvailable_Returns409() throws Exception {
        RoomResponse unavailableRoom = RoomResponse.builder()
                .id(1L)
                .hotelId(1L)
                .roomNumber("101")
                .pricePerNight(new BigDecimal("100.00"))
                .available(false)
                .build();

        when(hotelClient.getRoomById(1L)).thenReturn(unavailableRoom);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());

        assertThat(reservationRepository.count()).isEqualTo(0);
    }

    @Test
    void createReservation_InvalidData_Returns400() throws Exception {
        ReservationRequest invalidRequest = ReservationRequest.builder()
                .userId(null)
                .roomId(null)
                .guestName("")
                .guestEmail("")
                .checkInDate(null)
                .checkOutDate(null)
                .build();

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getReservationById_Success() throws Exception {
        when(hotelClient.getRoomById(1L)).thenReturn(availableRoom);
        when(hotelClient.updateRoomAvailability(anyLong(), anyBoolean())).thenReturn(availableRoom);
        doNothing().when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

        String response = mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andReturn().getResponse().getContentAsString();

        Long reservationId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/reservations/" + reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservationId))
                .andExpect(jsonPath("$.guestEmail").value("ana@example.com"));
    }

    @Test
    void getReservationById_NotFound_Returns404() throws Exception {
        mockMvc.perform(get("/api/reservations/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getReservationByNumber_Success() throws Exception {
        when(hotelClient.getRoomById(1L)).thenReturn(availableRoom);
        when(hotelClient.updateRoomAvailability(anyLong(), anyBoolean())).thenReturn(availableRoom);
        doNothing().when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

        String response = mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andReturn().getResponse().getContentAsString();

        String reservationNumber = objectMapper.readTree(response).get("reservationNumber").asText();

        mockMvc.perform(get("/api/reservations/number/" + reservationNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationNumber").value(reservationNumber));
    }

    @Test
    void getAllReservations_Success() throws Exception {
        when(hotelClient.getRoomById(anyLong())).thenReturn(availableRoom);
        when(hotelClient.updateRoomAvailability(anyLong(), anyBoolean())).thenReturn(availableRoom);
        doNothing().when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)));

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)));

        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getReservationsByUser_Success() throws Exception {
        when(hotelClient.getRoomById(anyLong())).thenReturn(availableRoom);
        when(hotelClient.updateRoomAvailability(anyLong(), anyBoolean())).thenReturn(availableRoom);
        doNothing().when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)));

        ReservationRequest otherUserRequest = ReservationRequest.builder()
                .userId(99L)
                .roomId(1L)
                .guestName("Marko Markovic")
                .guestEmail("marko@example.com")
                .checkInDate(LocalDate.now().plusDays(10))
                .checkOutDate(LocalDate.now().plusDays(12))
                .build();

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otherUserRequest)));

        mockMvc.perform(get("/api/reservations/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].guestEmail").value("ana@example.com"));
    }

    @Test
    void cancelReservation_Success() throws Exception {
        when(hotelClient.getRoomById(1L)).thenReturn(availableRoom);
        when(hotelClient.updateRoomAvailability(anyLong(), anyBoolean())).thenReturn(availableRoom);
        doNothing().when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

        String response = mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andReturn().getResponse().getContentAsString();

        Long reservationId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(patch("/api/reservations/" + reservationId + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        Reservation cancelled = reservationRepository.findById(reservationId).orElseThrow();
        assertThat(cancelled.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    void cancelReservation_NotFound_Returns404() throws Exception {
        mockMvc.perform(patch("/api/reservations/9999/cancel"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelReservation_AlreadyCancelled_Returns400() throws Exception {
        when(hotelClient.getRoomById(1L)).thenReturn(availableRoom);
        when(hotelClient.updateRoomAvailability(anyLong(), anyBoolean())).thenReturn(availableRoom);
        doNothing().when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

        String response = mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andReturn().getResponse().getContentAsString();

        Long reservationId = objectMapper.readTree(response).get("id").asLong();

        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        mockMvc.perform(patch("/api/reservations/" + reservationId + "/cancel"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Reservation is already cancelled"));
    }
}
