package com.slotsync.backend.service.impl;

import com.slotsync.backend.domain.Booking;
import com.slotsync.backend.domain.Slot;
import com.slotsync.backend.domain.User;
import com.slotsync.backend.domain.enums.BookingStatus;
import com.slotsync.backend.domain.enums.NotificationType;
import com.slotsync.backend.domain.enums.SlotStatus;
import com.slotsync.backend.dto.request.BookingRequest;
import com.slotsync.backend.dto.response.BookingResponse;
import com.slotsync.backend.exception.DuplicateBookingException;
import com.slotsync.backend.exception.ResourceNotFoundException;
import com.slotsync.backend.exception.SlotNotAvailableException;
import com.slotsync.backend.event.BookingCancelledEvent;
import com.slotsync.backend.event.BookingConfirmedEvent;
import com.slotsync.backend.mapper.BookingMapper;
import com.slotsync.backend.repository.BookingRepository;
import com.slotsync.backend.repository.SlotRepository;
import com.slotsync.backend.repository.UserRepository;
import com.slotsync.backend.service.BookingService;
import com.slotsync.backend.service.NotificationService;
import com.slotsync.backend.service.WaitlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final NotificationService notificationService;
    private final WaitlistService waitlistService;
    private final ApplicationEventPublisher eventPublisher;


    @Override
    @Transactional
    public BookingResponse createBooking(UUID customerId, BookingRequest request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // Design Requirement: Lock slot to prevent concurrent double-booking
        Slot slot = slotRepository.findByIdWithPessimisticLock(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found"));

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new SlotNotAvailableException("Slot is already booked or blocked");
        }

        // Prevent customer duplicate bookings on same slot
        if (bookingRepository.existsBySlotIdAndStatusNot(slot.getId(), BookingStatus.CANCELLED)) {
            throw new DuplicateBookingException("You already have an active booking on this slot");
        }

        slot.setStatus(SlotStatus.BOOKED);
        slotRepository.save(slot);

        Booking booking = new Booking();
        booking.setSlot(slot);
        booking.setCustomer(customer);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setNotes(request.getNotes());
        booking.setBookedAt(Instant.now());

        Booking savedBooking = bookingRepository.save(booking);

        // Publish domain event (handled asynchronously by NotificationEventListener)
        eventPublisher.publishEvent(new BookingConfirmedEvent(this, savedBooking));

        return bookingMapper.toResponse(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByCustomer(UUID customerId) {
        return bookingRepository.findByCustomerId(customerId)
                .stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelBooking(UUID customerId, UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return;
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(Instant.now());
        bookingRepository.save(booking);

        Slot slot = booking.getSlot();
        slot.setStatus(SlotStatus.AVAILABLE);
        slotRepository.save(slot);

        // Publish domain event
        eventPublisher.publishEvent(new BookingCancelledEvent(this, booking));

        // Design Requirement: FIFO Waitlist Promotion
        waitlistService.promoteNextInLine(slot.getId());
    }
}
