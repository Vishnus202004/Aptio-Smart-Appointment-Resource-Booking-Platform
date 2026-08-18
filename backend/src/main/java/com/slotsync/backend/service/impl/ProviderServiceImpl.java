package com.slotsync.backend.service.impl;

import com.slotsync.backend.domain.ProviderProfile;
import com.slotsync.backend.domain.enums.BookingStatus;
import com.slotsync.backend.domain.enums.ProviderCategory;
import com.slotsync.backend.domain.enums.SlotStatus;
import com.slotsync.backend.dto.request.UpdateProviderProfileRequest;
import com.slotsync.backend.dto.response.ProviderDashboardStats;
import com.slotsync.backend.dto.response.ProviderProfileResponse;
import com.slotsync.backend.exception.ResourceNotFoundException;
import com.slotsync.backend.mapper.ProviderProfileMapper;
import com.slotsync.backend.repository.BookingRepository;
import com.slotsync.backend.repository.ProviderProfileRepository;
import com.slotsync.backend.repository.SlotRepository;
import com.slotsync.backend.service.ProviderService;
import com.slotsync.backend.specification.ProviderProfileSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {

    private final ProviderProfileRepository providerProfileRepository;
    private final SlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final ProviderProfileMapper providerProfileMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProviderProfileResponse> searchProviders(String search, String category, String location, Pageable pageable) {
        ProviderCategory cat = null;
        if (category != null && !category.isBlank()) {
            try { cat = ProviderCategory.valueOf(category.toUpperCase()); } catch (IllegalArgumentException ignored) {}
        }

        Specification<ProviderProfile> spec = Specification
                .where(ProviderProfileSpecification.searchByNameOrDescription(search))
                .and(ProviderProfileSpecification.hasCategory(cat))
                .and(ProviderProfileSpecification.hasLocation(location));

        return providerProfileRepository.findAll(spec, pageable).map(providerProfileMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProviderProfileResponse getProviderById(UUID id) {
        ProviderProfile profile = providerProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        return providerProfileMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public ProviderProfileResponse updateProvider(UUID userId, UpdateProviderProfileRequest request) {
        ProviderProfile profile = providerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found for this user"));

        profile.setName(request.getName());
        profile.setDescription(request.getDescription());
        profile.setCategory(request.getCategory());
        profile.setLocation(request.getLocation());
        profile.setTimezone(request.getTimezone());
        profile.setVacationMode(request.isVacationMode());
        profile.setVacationStart(request.getVacationStart());
        profile.setVacationEnd(request.getVacationEnd());

        return providerProfileMapper.toResponse(providerProfileRepository.save(profile));
    }

    @Override
    @Transactional(readOnly = true)
    public ProviderDashboardStats getDashboardStats(UUID providerProfileId) {
        Instant now = Instant.now();
        Instant start = now.minus(30, ChronoUnit.DAYS);

        long totalSlots = slotRepository.findByProviderProfileIdAndStartTimeBetween(providerProfileId, start, now.plus(365, ChronoUnit.DAYS)).size();
        long bookedSlots = slotRepository.findByProviderProfileIdAndStartTimeBetween(providerProfileId, start, now.plus(365, ChronoUnit.DAYS))
                .stream().filter(s -> s.getStatus() == SlotStatus.BOOKED).count();
        long availableSlots = totalSlots - bookedSlots;
        double occupancyRate = totalSlots > 0 ? (double) bookedSlots / totalSlots * 100 : 0;

        long totalBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getSlot().getProviderProfile().getId().equals(providerProfileId))
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .count();

        return ProviderDashboardStats.builder()
                .totalSlots(totalSlots)
                .bookedSlots(bookedSlots)
                .availableSlots(availableSlots)
                .occupancyRate(occupancyRate)
                .totalBookings(totalBookings)
                .build();
    }
}
