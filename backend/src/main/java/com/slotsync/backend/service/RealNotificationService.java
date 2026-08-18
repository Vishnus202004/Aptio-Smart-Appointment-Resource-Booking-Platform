package com.slotsync.backend.service;

import com.slotsync.backend.domain.User;
import com.slotsync.backend.domain.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RealNotificationService extends NotificationService {
    Page<com.slotsync.backend.domain.Notification> getUserNotifications(UUID userId, Pageable pageable);
    void markAsRead(UUID notificationId);
}
