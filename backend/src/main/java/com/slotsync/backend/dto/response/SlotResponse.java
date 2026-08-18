package com.slotsync.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotResponse {
    private UUID id;
    private UUID providerProfileId;
    private Instant startTime;
    private Instant endTime;
    private int durationMinutes;
    private String status;
    private String title;
    private String notes;
}
