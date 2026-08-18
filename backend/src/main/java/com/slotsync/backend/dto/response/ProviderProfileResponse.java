package com.slotsync.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderProfileResponse {
    private UUID id;
    private UUID userId;
    private String name;
    private String description;
    private String category;
    private String location;
    private String timezone;
    private boolean vacationMode;
    private LocalDate vacationStart;
    private LocalDate vacationEnd;
}
