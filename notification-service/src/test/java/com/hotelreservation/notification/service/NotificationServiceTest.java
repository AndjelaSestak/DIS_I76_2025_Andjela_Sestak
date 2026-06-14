package com.hotelreservation.notification.service;

import com.hotelreservation.notification.dto.NotificationResponse;
import com.hotelreservation.notification.event.PaymentEvent;
import com.hotelreservation.notification.exception.NotificationNotFoundException;
import com.hotelreservation.notification.model.Notification;
import com.hotelreservation.notification.model.NotificationStatus;
import com.hotelreservation.notification.model.NotificationType;
import com.hotelreservation.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private PaymentEvent paymentEvent;
    private Notification notification;

    @BeforeEach
    void setUp() {
        paymentEvent = PaymentEvent.builder()
                .paymentNumber("PAY-ABCD1234")
                .reservationNumber("RES-001")
                .userId(1L)
                .guestName("John Doe")
                .guestEmail("john@example.com")
                .amount(new BigDecimal("200.00"))
                .paymentStatus("COMPLETED")
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(3))
                .build();

        notification = Notification.builder()
                .id(1L)
                .type(NotificationType.EMAIL)
                .recipient("john@example.com")
                .subject("Payment Confirmation - Reservation RES-001")
                .message("Dear John Doe, Your payment has been COMPLETED.")
                .status(NotificationStatus.SENT)
                .relatedEntityId("PAY-ABCD1234")
                .createdAt(LocalDateTime.now())
                .sentAt(LocalDateTime.now())
                .build();
    }

    @Test
    void sendPaymentConfirmation_SavesAndReturnsResponse() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        NotificationResponse response = notificationService.sendPaymentConfirmation(paymentEvent);

        assertThat(response).isNotNull();
        assertThat(response.getRecipient()).isEqualTo("john@example.com");
        assertThat(response.getRelatedEntityId()).isEqualTo("PAY-ABCD1234");
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void getAllNotifications_ReturnsAll() {
        when(notificationRepository.findAll()).thenReturn(List.of(notification));

        List<NotificationResponse> responses = notificationService.getAllNotifications();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getType()).isEqualTo(NotificationType.EMAIL);
    }

    @Test
    void getNotificationById_Found() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        NotificationResponse response = notificationService.getNotificationById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo(NotificationStatus.SENT);
    }

    @Test
    void getNotificationById_NotFound_ThrowsException() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.getNotificationById(99L))
                .isInstanceOf(NotificationNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getNotificationsByRecipient_ReturnsMatching() {
        when(notificationRepository.findByRecipient("john@example.com")).thenReturn(List.of(notification));

        List<NotificationResponse> responses = notificationService.getNotificationsByRecipient("john@example.com");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getRecipient()).isEqualTo("john@example.com");
    }

    @Test
    void getNotificationsByStatus_ReturnsMatching() {
        when(notificationRepository.findByStatus(NotificationStatus.SENT)).thenReturn(List.of(notification));

        List<NotificationResponse> responses = notificationService.getNotificationsByStatus(NotificationStatus.SENT);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getStatus()).isEqualTo(NotificationStatus.SENT);
    }

    @Test
    void getNotificationsByRelatedEntity_ReturnsMatching() {
        when(notificationRepository.findByRelatedEntityId("PAY-ABCD1234")).thenReturn(List.of(notification));

        List<NotificationResponse> responses = notificationService.getNotificationsByRelatedEntity("PAY-ABCD1234");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getRelatedEntityId()).isEqualTo("PAY-ABCD1234");
    }
}
