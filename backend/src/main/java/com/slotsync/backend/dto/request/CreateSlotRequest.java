package com.slotsync.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class CreateSlotRequest {

    @NotNull(message = "Provider profile ID is required")
    private UUID providerProfileId;

    @NotNull(message = "Start time is required")
    private Instant startTime;

    @NotNull(message = "End time is required")
    private Instant endTime;

    private String title;
    private String notes;
}
