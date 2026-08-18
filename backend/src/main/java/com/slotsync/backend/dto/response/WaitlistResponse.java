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
public class WaitlistResponse {
    private UUID id;
    private UUID slotId;
    private UUID customerId;
    private int position;
    private String status;
    private Instant joinedAt;
}
