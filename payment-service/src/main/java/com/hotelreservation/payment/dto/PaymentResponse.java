package com.hotelreservation.payment.dto;

import com.hotelreservation.payment.model.Payment;
import com.hotelreservation.payment.model.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {

    private Long id;
    private String paymentNumber;
    private String reservationNumber;
    private Long userId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String guestEmail;
    private LocalDateTime createdAt;

    public static PaymentResponse fromPayment(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentNumber(payment.getPaymentNumber())
                .reservationNumber(payment.getReservationNumber())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .guestEmail(payment.getGuestEmail())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
