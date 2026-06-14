package com.hotelreservation.notification.messaging;

import com.hotelreservation.notification.event.PaymentEvent;
import com.hotelreservation.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "payment.completed.queue")
    public void handlePaymentCompleted(PaymentEvent event) {
        log.info("Received payment event for reservation: {}", event.getReservationNumber());
        notificationService.sendPaymentConfirmation(event);
    }
}
