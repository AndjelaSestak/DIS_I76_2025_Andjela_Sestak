package com.hotelreservation.reservation.controller;

import com.hotelreservation.reservation.dto.ReservationRequest;
import com.hotelreservation.reservation.dto.ReservationResponse;
import com.hotelreservation.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "Reservation management operations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @Operation(summary = "Create a new reservation")
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reservation by ID")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @GetMapping("/number/{reservationNumber}")
    @Operation(summary = "Get reservation by reservation number")
    public ResponseEntity<ReservationResponse> getReservationByNumber(@PathVariable String reservationNumber) {
        return ResponseEntity.ok(reservationService.getReservationByNumber(reservationNumber));
    }

    @GetMapping
    @Operation(summary = "Get all reservations")
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reservations by user ID")
    public ResponseEntity<List<ReservationResponse>> getReservationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getReservationsByUser(userId));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a reservation")
    public ResponseEntity<ReservationResponse> cancelReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.cancelReservation(id));
    }
}
