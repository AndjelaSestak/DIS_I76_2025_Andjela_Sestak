package com.hotelreservation.reservation.dto;

import com.hotelreservation.reservation.model.Reservation;
import com.hotelreservation.reservation.model.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {

    private Long id;
    private String reservationNumber;
    private Long userId;
    private Long roomId;
    private Long hotelId;
    private String guestName;
    private String guestEmail;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal totalPrice;
    private ReservationStatus status;
    private LocalDateTime createdAt;

    public static ReservationResponse fromReservation(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .reservationNumber(reservation.getReservationNumber())
                .userId(reservation.getUserId())
                .roomId(reservation.getRoomId())
                .hotelId(reservation.getHotelId())
                .guestName(reservation.getGuestName())
                .guestEmail(reservation.getGuestEmail())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}
