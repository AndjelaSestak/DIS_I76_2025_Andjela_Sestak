package com.hotelreservation.notification.repository;

import com.hotelreservation.notification.model.Notification;
import com.hotelreservation.notification.model.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipient(String recipient);

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findByRelatedEntityId(String relatedEntityId);
}
