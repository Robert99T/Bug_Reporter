package com.bug.notification.repository;

import com.bug.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Standard CRUD — nothing custom needed
}