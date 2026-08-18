package com.slotsync.backend.service.impl;

import com.slotsync.backend.domain.Booking;
import com.slotsync.backend.domain.Slot;
import com.slotsync.backend.domain.User;
import com.slotsync.backend.domain.Waitlist;
import com.slotsync.backend.domain.enums.BookingStatus;
import com.slotsync.backend.domain.enums.NotificationType;
import com.slotsync.backend.domain.enums.SlotStatus;
import com.slotsync.backend.domain.enums.WaitlistStatus;
import com.slotsync.backend.dto.response.WaitlistResponse;
import com.slotsync.backend.exception.BadRequestException;
import com.slotsync.backend.exception.ResourceNotFoundException;
import com.slotsync.backend.mapper.WaitlistMapper;
import com.slotsync.backend.repository.BookingRepository;
import com.slotsync.backend.repository.SlotRepository;
import com.slotsync.backend.repository.UserRepository;
import com.slotsync.backend.repository.WaitlistRepository;
import com.slotsync.backend.service.NotificationService;
import com.slotsync.backend.service.WaitlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WaitlistServiceImpl implements WaitlistService {

    private final WaitlistRepository waitlistRepository;
    private final SlotRepository slotRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final WaitlistMapper waitlistMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public WaitlistResponse joinWaitlist(UUID customerId, UUID slotId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found"));

        if (slot.getStatus() != SlotStatus.BOOKED) {
            throw new BadRequestException("Slot is not booked. You can book it directly.");
        }

        // Check if customer is already on the waitlist for this slot
        if (waitlistRepository.findBySlotIdAndCustomerIdAndStatus(slotId, customerId, WaitlistStatus.WAITING).isPresent()) {
            throw new BadRequestException("You are already on the waitlist for this slot");
        }

        int nextPosition = waitlistRepository.findMaxPositionBySlotIdAndStatus(slotId, WaitlistStatus.WAITING) + 1;

        Waitlist waitlist = new Waitlist();
        waitlist.setSlot(slot);
        waitlist.setCustomer(customer);
        waitlist.setPosition(nextPosition);
        waitlist.setStatus(WaitlistStatus.WAITING);
        waitlist.setJoinedAt(Instant.now());

        Waitlist savedWaitlist = waitlistRepository.save(waitlist);
        return waitlistMapper.toResponse(savedWaitlist);
    }

    @Override
    @Transactional
    public void promoteNextInLine(UUID slotId) {
        List<Waitlist> queue = waitlistRepository.findBySlotIdAndStatusOrderByPositionAsc(slotId, WaitlistStatus.WAITING);
        if (queue.isEmpty()) {
            return;
        }

        // FIFO Promotion: First user in the list
        Waitlist candidate = queue.get(0);
        candidate.setStatus(WaitlistStatus.PROMOTED);
        candidate.setPromotedAt(Instant.now());
        waitlistRepository.save(candidate);

        Slot slot = candidate.getSlot();
        slot.setStatus(SlotStatus.BOOKED);
        slotRepository.save(slot);

        Booking booking = new Booking();
        booking.setSlot(slot);
        booking.setCustomer(candidate.getCustomer());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookedAt(Instant.now());
        bookingRepository.save(booking);

        notificationService.sendNotification(
                candidate.getCustomer(),
                NotificationType.WAITLIST_PROMOTED,
                "Waitlist Promotion",
                "Great news! You have been promoted from the waitlist and your booking is confirmed."
        );
    }
}
