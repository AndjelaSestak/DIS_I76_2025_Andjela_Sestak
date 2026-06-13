package com.hotelreservation.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user-service")
    public ResponseEntity<Map<String, String>> userServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "User Service is currently unavailable. Please try again later."));
    }

    @GetMapping("/hotel-service")
    public ResponseEntity<Map<String, String>> hotelServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "Hotel Service is currently unavailable. Please try again later."));
    }

    @GetMapping("/reservation-service")
    public ResponseEntity<Map<String, String>> reservationServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "Reservation Service is currently unavailable. Please try again later."));
    }

    @GetMapping("/payment-service")
    public ResponseEntity<Map<String, String>> paymentServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "Payment Service is currently unavailable. Please try again later."));
    }
}
