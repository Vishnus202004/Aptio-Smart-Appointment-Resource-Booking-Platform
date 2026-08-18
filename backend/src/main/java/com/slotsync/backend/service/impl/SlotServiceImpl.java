package com.slotsync.backend.service.impl;

import com.slotsync.backend.domain.ProviderProfile;
import com.slotsync.backend.domain.Slot;
import com.slotsync.backend.domain.enums.SlotStatus;
import com.slotsync.backend.dto.request.CreateSlotRequest;
import com.slotsync.backend.dto.response.SlotResponse;
import com.slotsync.backend.exception.ResourceNotFoundException;
import com.slotsync.backend.mapper.SlotMapper;
import com.slotsync.backend.repository.ProviderProfileRepository;
import com.slotsync.backend.repository.SlotRepository;
import com.slotsync.backend.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SlotServiceImpl implements SlotService {

    private final SlotRepository slotRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final SlotMapper slotMapper;

    @Override
    @Transactional
    public SlotResponse createSlot(CreateSlotRequest request) {
        ProviderProfile provider = providerProfileRepository.findById(request.getProviderProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));

        Slot slot = new Slot();
        slot.setProviderProfile(provider);
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setDurationMinutes((int) Duration.between(request.getStartTime(), request.getEndTime()).toMinutes());
        slot.setStatus(SlotStatus.AVAILABLE);
        slot.setTitle(request.getTitle());
        slot.setNotes(request.getNotes());

        Slot savedSlot = slotRepository.save(slot);
        return slotMapper.toResponse(savedSlot);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SlotResponse> getSlotsByProviderAndDateRange(UUID providerProfileId, Instant start, Instant end) {
        return slotRepository.findByProviderProfileIdAndStartTimeBetween(providerProfileId, start, end)
                .stream()
                .map(slotMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSlot(UUID slotId) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found"));
        slotRepository.delete(slot);
    }
}
