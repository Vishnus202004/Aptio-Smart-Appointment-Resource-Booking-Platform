package com.slotsync.backend.service.impl;

import com.slotsync.backend.domain.User;
import com.slotsync.backend.domain.enums.NotificationType;
import com.slotsync.backend.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MockNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(MockNotificationService.class);

    @Override
    public void sendNotification(User recipient, NotificationType type, String title, String message) {
        log.info("Notification sent to user {}: [{}] {} - {}", recipient.getEmail(), type, title, message);
    }
}
