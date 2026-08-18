package com.slotsync.backend.service;

import com.slotsync.backend.dto.request.UpdateProviderProfileRequest;
import com.slotsync.backend.dto.response.ProviderDashboardStats;
import com.slotsync.backend.dto.response.ProviderProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProviderService {
    Page<ProviderProfileResponse> searchProviders(String search, String category, String location, Pageable pageable);
    ProviderProfileResponse getProviderById(UUID id);
    ProviderProfileResponse updateProvider(UUID userId, UpdateProviderProfileRequest request);
    ProviderDashboardStats getDashboardStats(UUID providerProfileId);
}
