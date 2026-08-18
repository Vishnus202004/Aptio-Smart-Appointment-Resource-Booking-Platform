package com.slotsync.backend.service;

import com.slotsync.backend.domain.User;
import com.slotsync.backend.domain.enums.NotificationType;

public interface NotificationService {
    void sendNotification(User recipient, NotificationType type, String title, String message);
}
