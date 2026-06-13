package com.hotelreservation.hotel.controller;

import com.hotelreservation.hotel.dto.RoomRequest;
import com.hotelreservation.hotel.dto.RoomResponse;
import com.hotelreservation.hotel.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Rooms", description = "Room management operations")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    @Operation(summary = "Create a new room")
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody RoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @GetMapping("/hotel/{hotelId}")
    @Operation(summary = "Get all rooms for a hotel")
    public ResponseEntity<List<RoomResponse>> getRoomsByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomsByHotel(hotelId));
    }

    @GetMapping("/hotel/{hotelId}/available")
    @Operation(summary = "Get available rooms for a hotel")
    public ResponseEntity<List<RoomResponse>> getAvailableRoomsByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.getAvailableRoomsByHotel(hotelId));
    }

    @GetMapping("/available")
    @Operation(summary = "Get all available rooms")
    public ResponseEntity<List<RoomResponse>> getAllAvailableRooms() {
        return ResponseEntity.ok(roomService.getAllAvailableRooms());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update room details")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long id,
                                                    @RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    @PatchMapping("/{id}/availability")
    @Operation(summary = "Update room availability (internal use)")
    public ResponseEntity<RoomResponse> updateRoomAvailability(@PathVariable Long id,
                                                                @RequestParam Boolean available) {
        return ResponseEntity.ok(roomService.updateRoomAvailability(id, available));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a room")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
