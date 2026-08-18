package com.slotsync.backend.domain;

import com.slotsync.backend.domain.enums.ProviderCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "provider_profiles", indexes = {
    @Index(name = "idx_provider_user", columnList = "user_id", unique = true)
})
@Getter
@Setter
public class ProviderProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ProviderCategory category;

    @Column(name = "location")
    private String location;

    @Column(name = "timezone", nullable = false)
    private String timezone;

    @Column(name = "vacation_mode", nullable = false)
    private boolean vacationMode = false;

    @Column(name = "vacation_start")
    private LocalDate vacationStart;

    @Column(name = "vacation_end")
    private LocalDate vacationEnd;
}
