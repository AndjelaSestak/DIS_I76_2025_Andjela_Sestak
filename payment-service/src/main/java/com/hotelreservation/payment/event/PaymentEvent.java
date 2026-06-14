package com.hotelreservation.payment.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {

    private String paymentNumber;
    private String reservationNumber;
    private Long userId;
    private String guestName;
    private String guestEmail;
    private BigDecimal amount;
    private String paymentStatus;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
