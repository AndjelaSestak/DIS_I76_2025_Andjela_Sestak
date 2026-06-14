package com.hotelreservation.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {

    private Long id;
    private Long hotelId;
    private String hotelName;
    private String roomNumber;
    private String type;
    private BigDecimal pricePerNight;
    private Integer capacity;
    private String description;
    private Boolean available;
}
