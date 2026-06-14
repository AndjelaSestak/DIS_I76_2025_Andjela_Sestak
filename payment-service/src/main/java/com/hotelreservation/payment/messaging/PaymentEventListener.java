package com.hotelreservation.payment.messaging;

import com.hotelreservation.payment.event.ReservationEvent;
import com.hotelreservation.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentService paymentService;

    @RabbitListener(queues = "reservation.created.queue")
    public void handleReservationCreated(ReservationEvent event) {
        log.info("Received reservation event for: {}", event.getReservationNumber());
        try {
            paymentService.processPayment(event);
        } catch (Exception ex) {
            log.error("Failed to process payment for reservation {}: {}", event.getReservationNumber(), ex.getMessage());
        }
    }
}
