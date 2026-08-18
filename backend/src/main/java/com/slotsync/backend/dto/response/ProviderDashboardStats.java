package com.slotsync.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderDashboardStats {
    private long totalSlots;
    private long bookedSlots;
    private long availableSlots;
    private double occupancyRate;
    private long totalBookings;
}
