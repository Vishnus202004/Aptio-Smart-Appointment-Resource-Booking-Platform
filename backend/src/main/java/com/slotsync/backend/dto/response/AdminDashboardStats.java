package com.slotsync.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStats {
    private long totalUsers;
    private long totalProviders;
    private long totalBookings;
    private long totalActiveBookings;
    private long totalCancelledBookings;
    private long totalSlots;
    private long totalAvailableSlots;
}
