package com.hotelreservation.notification.controller;

import com.hotelreservation.notification.dto.NotificationResponse;
import com.hotelreservation.notification.model.NotificationStatus;
import com.hotelreservation.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(
            @RequestParam(required = false) NotificationStatus status) {
        if (status != null) {
            return ResponseEntity.ok(notificationService.getNotificationsByStatus(status));
        }
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByRecipient(@PathVariable String recipient) {
        return ResponseEntity.ok(notificationService.getNotificationsByRecipient(recipient));
    }

    @GetMapping("/entity/{relatedEntityId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByEntity(@PathVariable String relatedEntityId) {
        return ResponseEntity.ok(notificationService.getNotificationsByRelatedEntity(relatedEntityId));
    }
}
