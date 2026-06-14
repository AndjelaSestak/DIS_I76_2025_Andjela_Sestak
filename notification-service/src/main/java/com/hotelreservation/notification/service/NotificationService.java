package com.hotelreservation.notification.service;

import com.hotelreservation.notification.dto.NotificationResponse;
import com.hotelreservation.notification.event.PaymentEvent;
import com.hotelreservation.notification.exception.NotificationNotFoundException;
import com.hotelreservation.notification.model.Notification;
import com.hotelreservation.notification.model.NotificationStatus;
import com.hotelreservation.notification.model.NotificationType;
import com.hotelreservation.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public NotificationResponse sendPaymentConfirmation(PaymentEvent event) {
        String subject = "Payment Confirmation - Reservation " + event.getReservationNumber();
        String message = String.format(
                "Dear %s,%nYour payment %s has been %s.%nReservation: %s%nCheck-in: %s | Check-out: %s%nAmount paid: %.2f EUR%nThank you for choosing our hotel!",
                event.getGuestName(),
                event.getPaymentNumber(),
                event.getPaymentStatus(),
                event.getReservationNumber(),
                event.getCheckInDate(),
                event.getCheckOutDate(),
                event.getAmount()
        );

        Notification notification = Notification.builder()
                .type(NotificationType.EMAIL)
                .recipient(event.getGuestEmail())
                .subject(subject)
                .message(message)
                .status(NotificationStatus.PENDING)
                .relatedEntityId(event.getPaymentNumber())
                .build();

        boolean sent = Math.random() > 0.1;
        if (sent) {
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            log.info("Email notification SENT to {} for payment {}", event.getGuestEmail(), event.getPaymentNumber());
        } else {
            notification.setStatus(NotificationStatus.FAILED);
            log.warn("Email notification FAILED for payment {}", event.getPaymentNumber());
        }

        return NotificationResponse.fromEntity(notificationRepository.save(notification));
    }

    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    public NotificationResponse getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
        return NotificationResponse.fromEntity(notification);
    }

    public List<NotificationResponse> getNotificationsByRecipient(String recipient) {
        return notificationRepository.findByRecipient(recipient).stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    public List<NotificationResponse> getNotificationsByStatus(NotificationStatus status) {
        return notificationRepository.findByStatus(status).stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }

    public List<NotificationResponse> getNotificationsByRelatedEntity(String relatedEntityId) {
        return notificationRepository.findByRelatedEntityId(relatedEntityId).stream()
                .map(NotificationResponse::fromEntity)
                .toList();
    }
}
