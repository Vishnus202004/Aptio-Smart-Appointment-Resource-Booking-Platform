package com.slotsync.backend.service;

import com.slotsync.backend.dto.request.CreateSlotRequest;
import com.slotsync.backend.dto.response.SlotResponse;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SlotService {
    SlotResponse createSlot(CreateSlotRequest request);
    List<SlotResponse> getSlotsByProviderAndDateRange(UUID providerProfileId, Instant start, Instant end);
    void deleteSlot(UUID slotId);
}
