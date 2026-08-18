package com.slotsync.backend.service.impl;

import com.slotsync.backend.domain.Notification;
import com.slotsync.backend.domain.User;
import com.slotsync.backend.domain.enums.NotificationType;
import com.slotsync.backend.mapper.SlotMapper;
import com.slotsync.backend.repository.NotificationRepository;
import com.slotsync.backend.repository.SlotRepository;
import com.slotsync.backend.service.RealNotificationService;
import com.slotsync.backend.websocket.SlotBroadcastService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class NotificationServiceImpl implements RealNotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final SlotBroadcastService slotBroadcastService;

    @Override
    @Transactional
    public void sendNotification(User recipient, NotificationType type, String title, String message) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notificationRepository.save(notification);

        // Real-time push via WebSocket
        slotBroadcastService.sendUserNotification(recipient.getId(), title + ": " + message);
        log.info("Notification [{}] sent to user {}", type, recipient.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getUserNotifications(UUID userId, Pageable pageable) {
        return notificationRepository.findByRecipientId(userId, pageable);
    }

    @Override
    @Transactional
    public void markAsRead(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            n.setReadAt(Instant.now());
            notificationRepository.save(n);
        });
    }
}
