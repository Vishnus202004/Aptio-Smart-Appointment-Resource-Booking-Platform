package com.slotsync.backend.dto.request;

import com.slotsync.backend.domain.enums.ProviderCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProviderProfileRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Category is required")
    private ProviderCategory category;

    private String location;

    @NotBlank(message = "Timezone is required")
    private String timezone;

    private boolean vacationMode;
    private LocalDate vacationStart;
    private LocalDate vacationEnd;
}
