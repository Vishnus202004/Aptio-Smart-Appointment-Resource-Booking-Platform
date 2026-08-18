package com.slotsync.backend.websocket;

import com.slotsync.backend.dto.response.SlotResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SlotBroadcastService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastSlotChange(UUID providerId, SlotResponse slotResponse) {
        messagingTemplate.convertAndSend("/topic/slots/" + providerId, slotResponse);
    }

    public void sendUserNotification(UUID userId, String message) {
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", message);
    }
}
