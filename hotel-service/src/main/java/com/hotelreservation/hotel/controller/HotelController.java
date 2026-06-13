package com.hotelreservation.hotel.controller;

import com.hotelreservation.hotel.dto.HotelRequest;
import com.hotelreservation.hotel.dto.HotelResponse;
import com.hotelreservation.hotel.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@Tag(name = "Hotels", description = "Hotel management operations")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping
    @Operation(summary = "Create a new hotel")
    public ResponseEntity<HotelResponse> createHotel(@Valid @RequestBody HotelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.createHotel(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get hotel by ID")
    public ResponseEntity<HotelResponse> getHotelById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @GetMapping
    @Operation(summary = "Get all hotels")
    public ResponseEntity<List<HotelResponse>> getAllHotels(
            @RequestParam(required = false) Boolean activeOnly) {
        List<HotelResponse> hotels = Boolean.TRUE.equals(activeOnly)
                ? hotelService.getActiveHotels()
                : hotelService.getAllHotels();
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Get hotels by city")
    public ResponseEntity<List<HotelResponse>> getHotelsByCity(@PathVariable String city) {
        return ResponseEntity.ok(hotelService.getHotelsByCity(city));
    }

    @GetMapping("/stars/{minStars}")
    @Operation(summary = "Get hotels by minimum star rating")
    public ResponseEntity<List<HotelResponse>> getHotelsByMinStars(@PathVariable Integer minStars) {
        return ResponseEntity.ok(hotelService.getHotelsByMinStars(minStars));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update hotel information")
    public ResponseEntity<HotelResponse> updateHotel(@PathVariable Long id,
                                                      @RequestBody HotelRequest request) {
        return ResponseEntity.ok(hotelService.updateHotel(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a hotel")
    public ResponseEntity<Void> deactivateHotel(@PathVariable Long id) {
        hotelService.deactivateHotel(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a hotel")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }
}
