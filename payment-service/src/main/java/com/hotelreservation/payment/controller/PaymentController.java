package com.hotelreservation.payment.controller;

import com.hotelreservation.payment.dto.PaymentResponse;
import com.hotelreservation.payment.model.PaymentStatus;
import com.hotelreservation.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/number/{paymentNumber}")
    public ResponseEntity<PaymentResponse> getPaymentByNumber(@PathVariable String paymentNumber) {
        return ResponseEntity.ok(paymentService.getPaymentByNumber(paymentNumber));
    }

    @GetMapping("/reservation/{reservationNumber}")
    public ResponseEntity<PaymentResponse> getPaymentByReservation(@PathVariable String reservationNumber) {
        return ResponseEntity.ok(paymentService.getPaymentByReservationNumber(reservationNumber));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getPaymentsByUser(userId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        return ResponseEntity.ok(paymentService.getPaymentsByStatus(status));
    }
}
