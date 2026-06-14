package com.hotelreservation.payment.service;

import com.hotelreservation.payment.config.RabbitMQConfig;
import com.hotelreservation.payment.dto.PaymentResponse;
import com.hotelreservation.payment.event.PaymentEvent;
import com.hotelreservation.payment.event.ReservationEvent;
import com.hotelreservation.payment.exception.PaymentNotFoundException;
import com.hotelreservation.payment.model.Payment;
import com.hotelreservation.payment.model.PaymentStatus;
import com.hotelreservation.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public PaymentResponse processPayment(ReservationEvent event) {
        log.info("Processing payment for reservation: {}", event.getReservationNumber());

        String paymentNumber = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = Payment.builder()
                .paymentNumber(paymentNumber)
                .reservationNumber(event.getReservationNumber())
                .userId(event.getUserId())
                .amount(event.getTotalPrice())
                .status(PaymentStatus.COMPLETED)
                .guestEmail(event.getGuestEmail())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment {} saved with status COMPLETED", paymentNumber);

        PaymentEvent paymentEvent = PaymentEvent.builder()
                .paymentNumber(savedPayment.getPaymentNumber())
                .reservationNumber(savedPayment.getReservationNumber())
                .userId(savedPayment.getUserId())
                .guestName(event.getGuestName())
                .guestEmail(savedPayment.getGuestEmail())
                .amount(savedPayment.getAmount())
                .paymentStatus(savedPayment.getStatus().name())
                .checkInDate(event.getCheckInDate())
                .checkOutDate(event.getCheckOutDate())
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_COMPLETED_ROUTING_KEY,
                paymentEvent
        );
        log.info("PaymentEvent published for reservation: {}", event.getReservationNumber());

        return PaymentResponse.fromPayment(savedPayment);
    }

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(PaymentResponse::fromPayment)
                .toList();
    }

    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return PaymentResponse.fromPayment(payment);
    }

    public PaymentResponse getPaymentByNumber(String paymentNumber) {
        Payment payment = paymentRepository.findByPaymentNumber(paymentNumber)
                .orElseThrow(() -> new PaymentNotFoundException(paymentNumber));
        return PaymentResponse.fromPayment(payment);
    }

    public PaymentResponse getPaymentByReservationNumber(String reservationNumber) {
        Payment payment = paymentRepository.findByReservationNumber(reservationNumber)
                .orElseThrow(() -> new PaymentNotFoundException("Reservation: " + reservationNumber));
        return PaymentResponse.fromPayment(payment);
    }

    public List<PaymentResponse> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(PaymentResponse::fromPayment)
                .toList();
    }

    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status).stream()
                .map(PaymentResponse::fromPayment)
                .toList();
    }
}
