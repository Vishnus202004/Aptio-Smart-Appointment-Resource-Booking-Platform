package com.slotsync.backend.repository;

import com.slotsync.backend.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID>, JpaSpecificationExecutor<Booking> {
    List<Booking> findByCustomerId(UUID customerId);
    boolean existsBySlotIdAndStatusNot(UUID slotId, com.slotsync.backend.domain.enums.BookingStatus status);
}
