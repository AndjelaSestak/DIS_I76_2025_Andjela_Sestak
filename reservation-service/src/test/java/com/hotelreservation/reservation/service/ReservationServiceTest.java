package com.hotelreservation.reservation.service;

import com.hotelreservation.reservation.client.HotelClient;
import com.hotelreservation.reservation.dto.ReservationRequest;
import com.hotelreservation.reservation.dto.ReservationResponse;
import com.hotelreservation.reservation.dto.RoomResponse;
import com.hotelreservation.reservation.exception.ReservationNotFoundException;
import com.hotelreservation.reservation.exception.RoomNotAvailableException;
import com.hotelreservation.reservation.model.Reservation;
import com.hotelreservation.reservation.model.ReservationStatus;
import com.hotelreservation.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private HotelClient hotelClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ReservationService reservationService;

    private RoomResponse availableRoom;
    private RoomResponse unavailableRoom;
    private ReservationRequest request;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        availableRoom = RoomResponse.builder()
                .id(1L)
                .hotelId(1L)
                .hotelName("Grand Hotel")
                .roomNumber("101")
                .pricePerNight(new BigDecimal("120.00"))
                .available(true)
                .build();

        unavailableRoom = RoomResponse.builder()
                .id(2L)
                .hotelId(1L)
                .available(false)
                .build();

        request = ReservationRequest.builder()
                .userId(1L)
                .roomId(1L)
                .guestName("John Doe")
                .guestEmail("john@example.com")
                .checkInDate(LocalDate.now().plusDays(5))
                .checkOutDate(LocalDate.now().plusDays(8))
                .build();

        reservation = Reservation.builder()
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
    void createReservation_Success() {
        when(hotelClient.getRoomById(1L)).thenReturn(availableRoom);
        when(reservationRepository.existsByReservationNumber(anyString())).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(hotelClient.updateRoomAvailability(eq(1L), eq(false))).thenReturn(unavailableRoom);

        ReservationResponse response = reservationService.createReservation(request);

        assertThat(response).isNotNull();
        assertThat(response.getGuestName()).isEqualTo("John Doe");
        assertThat(response.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        verify(hotelClient).getRoomById(1L);
        verify(hotelClient).updateRoomAvailability(1L, false);
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    void createReservation_RoomNotAvailable_ThrowsException() {
        when(hotelClient.getRoomById(2L)).thenReturn(unavailableRoom);

        ReservationRequest badRequest = ReservationRequest.builder()
                .userId(1L).roomId(2L)
                .guestName("John").guestEmail("john@example.com")
                .checkInDate(LocalDate.now().plusDays(5))
                .checkOutDate(LocalDate.now().plusDays(8))
                .build();

        assertThatThrownBy(() -> reservationService.createReservation(badRequest))
                .isInstanceOf(RoomNotAvailableException.class);
    }

    @Test
    void createReservation_InvalidDates_ThrowsException() {
        when(hotelClient.getRoomById(1L)).thenReturn(availableRoom);

        ReservationRequest badRequest = ReservationRequest.builder()
                .userId(1L).roomId(1L)
                .guestName("John").guestEmail("john@example.com")
                .checkInDate(LocalDate.now().plusDays(8))
                .checkOutDate(LocalDate.now().plusDays(5))
                .build();

        assertThatThrownBy(() -> reservationService.createReservation(badRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Check-out date must be after check-in date");
    }

    @Test
    void getReservationById_Found() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        ReservationResponse response = reservationService.getReservationById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getReservationNumber()).isEqualTo("RES-ABCD1234");
    }

    @Test
    void getReservationById_NotFound_ThrowsException() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.getReservationById(99L))
                .isInstanceOf(ReservationNotFoundException.class);
    }

    @Test
    void getReservationsByUser_ReturnsList() {
        when(reservationRepository.findByUserId(1L)).thenReturn(List.of(reservation));

        List<ReservationResponse> responses = reservationService.getReservationsByUser(1L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getUserId()).isEqualTo(1L);
    }

    @Test
    void cancelReservation_Success() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(hotelClient.updateRoomAvailability(eq(1L), eq(true))).thenReturn(availableRoom);

        ReservationResponse response = reservationService.cancelReservation(1L);

        assertThat(response).isNotNull();
        verify(hotelClient).updateRoomAvailability(1L, true);
    }

    @Test
    void cancelReservation_AlreadyCancelled_ThrowsException() {
        reservation.setStatus(ReservationStatus.CANCELLED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.cancelReservation(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already cancelled");
    }

    @Test
    void getAllReservations_ReturnsList() {
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));

        List<ReservationResponse> responses = reservationService.getAllReservations();

        assertThat(responses).hasSize(1);
    }
}
