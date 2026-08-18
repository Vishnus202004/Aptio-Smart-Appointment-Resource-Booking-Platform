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
public class BookingResponse {
    private UUID id;
    private SlotResponse slot;
    private UserResponse customer;
    private String status;
    private String notes;
    private Instant bookedAt;
    private Instant cancelledAt;
    private String cancellationReason;
}
