package com.slotsync.backend.service;

import com.slotsync.backend.dto.response.WaitlistResponse;

import java.util.UUID;

public interface WaitlistService {
    WaitlistResponse joinWaitlist(UUID customerId, UUID slotId);
    void promoteNextInLine(UUID slotId);
}
