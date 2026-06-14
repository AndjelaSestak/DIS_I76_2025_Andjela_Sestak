package com.hotelreservation.notification.controller;

import com.hotelreservation.notification.dto.NotificationResponse;
import com.hotelreservation.notification.exception.GlobalExceptionHandler;
import com.hotelreservation.notification.exception.NotificationNotFoundException;
import com.hotelreservation.notification.model.NotificationStatus;
import com.hotelreservation.notification.model.NotificationType;
import com.hotelreservation.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@Import(GlobalExceptionHandler.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    private NotificationResponse notificationResponse;

    @BeforeEach
    void setUp() {
        notificationResponse = NotificationResponse.builder()
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
    void getAllNotifications_Returns200() throws Exception {
        when(notificationService.getAllNotifications()).thenReturn(List.of(notificationResponse));

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recipient").value("john@example.com"))
                .andExpect(jsonPath("$[0].status").value("SENT"));
    }

    @Test
    void getAllNotifications_WithStatusFilter_Returns200() throws Exception {
        when(notificationService.getNotificationsByStatus(NotificationStatus.SENT))
                .thenReturn(List.of(notificationResponse));

        mockMvc.perform(get("/api/notifications").param("status", "SENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("SENT"));
    }

    @Test
    void getNotificationById_Found_Returns200() throws Exception {
        when(notificationService.getNotificationById(1L)).thenReturn(notificationResponse);

        mockMvc.perform(get("/api/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("EMAIL"));
    }

    @Test
    void getNotificationById_NotFound_Returns404() throws Exception {
        when(notificationService.getNotificationById(99L))
                .thenThrow(new NotificationNotFoundException(99L));

        mockMvc.perform(get("/api/notifications/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getNotificationsByRecipient_Returns200() throws Exception {
        when(notificationService.getNotificationsByRecipient("john@example.com"))
                .thenReturn(List.of(notificationResponse));

        mockMvc.perform(get("/api/notifications/recipient/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recipient").value("john@example.com"));
    }

    @Test
    void getNotificationsByEntity_Returns200() throws Exception {
        when(notificationService.getNotificationsByRelatedEntity("PAY-ABCD1234"))
                .thenReturn(List.of(notificationResponse));

        mockMvc.perform(get("/api/notifications/entity/PAY-ABCD1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].relatedEntityId").value("PAY-ABCD1234"));
    }
}
