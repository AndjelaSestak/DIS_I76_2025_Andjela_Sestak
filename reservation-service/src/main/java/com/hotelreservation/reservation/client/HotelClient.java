package com.hotelreservation.reservation.client;

import com.hotelreservation.reservation.dto.RoomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hotel-service")
public interface HotelClient {

    @GetMapping("/api/rooms/{id}")
    RoomResponse getRoomById(@PathVariable Long id);

    @PutMapping("/api/rooms/{id}/availability")
    RoomResponse updateRoomAvailability(@PathVariable Long id, @RequestParam Boolean available);
}
