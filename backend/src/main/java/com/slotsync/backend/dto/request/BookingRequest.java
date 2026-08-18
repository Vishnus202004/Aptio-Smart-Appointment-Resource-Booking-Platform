package com.slotsync.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class BookingRequest {

    @NotNull(message = "Slot ID is required")
    private UUID slotId;

    private String notes;
}
