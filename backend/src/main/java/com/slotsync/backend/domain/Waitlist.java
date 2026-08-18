package com.slotsync.backend.domain;

import com.slotsync.backend.domain.enums.WaitlistStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "waitlist", indexes = {
    @Index(name = "idx_waitlist_slot_pos", columnList = "slot_id, position, status"),
    @Index(name = "idx_waitlist_customer", columnList = "customer_id")
})
@Getter
@Setter
public class Waitlist extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "slot_id", referencedColumnName = "id", nullable = false)
    private Slot slot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private User customer;

    @Column(name = "position", nullable = false)
    private int position;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WaitlistStatus status;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    @Column(name = "promoted_at")
    private Instant promotedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;
}
