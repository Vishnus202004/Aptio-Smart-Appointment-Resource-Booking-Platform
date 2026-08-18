package com.slotsync.backend.domain;

import com.slotsync.backend.domain.enums.SlotStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "slots", indexes = {
    @Index(name = "idx_slot_provider_status", columnList = "provider_profile_id, status"),
    @Index(name = "idx_slot_start_end", columnList = "start_time, end_time")
})
@Getter
@Setter
public class Slot extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provider_profile_id", referencedColumnName = "id", nullable = false)
    private ProviderProfile providerProfile;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SlotStatus status;

    @Column(name = "title")
    private String title;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
