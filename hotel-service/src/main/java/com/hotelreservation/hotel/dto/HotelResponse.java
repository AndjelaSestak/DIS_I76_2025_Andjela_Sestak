package com.hotelreservation.hotel.dto;

import com.hotelreservation.hotel.model.Hotel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelResponse {

    private Long id;
    private String name;
    private String city;
    private String address;
    private String description;
    private Integer stars;
    private String amenities;
    private Boolean active;
    private int totalRooms;
    private long availableRooms;
    private LocalDateTime createdAt;

    public static HotelResponse fromHotel(Hotel hotel) {
        return HotelResponse.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .city(hotel.getCity())
                .address(hotel.getAddress())
                .description(hotel.getDescription())
                .stars(hotel.getStars())
                .amenities(hotel.getAmenities())
                .active(hotel.getActive())
                .totalRooms(hotel.getRooms().size())
                .availableRooms(hotel.getRooms().stream().filter(r -> Boolean.TRUE.equals(r.getAvailable())).count())
                .createdAt(hotel.getCreatedAt())
                .build();
    }
}
