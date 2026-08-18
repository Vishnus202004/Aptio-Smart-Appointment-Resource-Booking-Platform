package com.slotsync.backend.repository;

import com.slotsync.backend.domain.Waitlist;
import com.slotsync.backend.domain.enums.WaitlistStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, UUID> {

    List<Waitlist> findBySlotIdAndStatusOrderByPositionAsc(UUID slotId, WaitlistStatus status);

    @Query("SELECT COALESCE(MAX(w.position), 0) FROM Waitlist w WHERE w.slot.id = :slotId AND w.status = :status")
    int findMaxPositionBySlotIdAndStatus(@Param("slotId") UUID slotId, @Param("status") WaitlistStatus status);

    Optional<Waitlist> findBySlotIdAndCustomerIdAndStatus(UUID slotId, UUID customerId, WaitlistStatus status);
}
