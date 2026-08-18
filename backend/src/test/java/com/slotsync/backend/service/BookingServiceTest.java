package com.slotsync.backend.service;

import com.slotsync.backend.domain.Booking;
import com.slotsync.backend.domain.Slot;
import com.slotsync.backend.domain.User;
import com.slotsync.backend.domain.enums.BookingStatus;
import com.slotsync.backend.domain.enums.Role;
import com.slotsync.backend.domain.enums.SlotStatus;
import com.slotsync.backend.domain.enums.UserStatus;
import com.slotsync.backend.dto.request.BookingRequest;
import com.slotsync.backend.dto.response.BookingResponse;
import com.slotsync.backend.exception.ResourceNotFoundException;
import com.slotsync.backend.exception.SlotNotAvailableException;
import com.slotsync.backend.mapper.BookingMapper;
import com.slotsync.backend.repository.BookingRepository;
import com.slotsync.backend.repository.SlotRepository;
import com.slotsync.backend.repository.UserRepository;
import com.slotsync.backend.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService Unit Tests")
class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private SlotRepository slotRepository;
    @Mock private UserRepository userRepository;
    @Mock private BookingMapper bookingMapper;
    @Mock private NotificationService notificationService;
    @Mock private WaitlistService waitlistService;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User customer;
    private Slot availableSlot;
    private UUID customerId;
    private UUID slotId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        slotId = UUID.randomUUID();

        customer = new User();
        customer.setId(customerId);
        customer.setEmail("customer@test.com");
        customer.setRole(Role.CUSTOMER);
        customer.setStatus(UserStatus.ACTIVE);
        customer.setFirstName("Test");
        customer.setLastName("User");
        customer.setPasswordHash("hash");

        availableSlot = new Slot();
        availableSlot.setId(slotId);
        availableSlot.setStatus(SlotStatus.AVAILABLE);
        availableSlot.setStartTime(Instant.now().plusSeconds(3600));
        availableSlot.setEndTime(Instant.now().plusSeconds(7200));
        availableSlot.setVersion(0L);
    }

    @Test
    @DisplayName("Should create booking successfully when slot is available")
    void createBooking_shouldSucceed_whenSlotAvailable() {
        BookingRequest request = new BookingRequest();
        request.setSlotId(slotId);

        Booking savedBooking = new Booking();
        savedBooking.setId(UUID.randomUUID());
        savedBooking.setSlot(availableSlot);
        savedBooking.setCustomer(customer);
        savedBooking.setStatus(BookingStatus.CONFIRMED);
        savedBooking.setBookedAt(Instant.now());

        BookingResponse expectedResponse = new BookingResponse();

        when(userRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(slotRepository.findByIdWithPessimisticLock(slotId)).thenReturn(Optional.of(availableSlot));
        when(bookingRepository.existsBySlotIdAndStatusNot(slotId, BookingStatus.CANCELLED)).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
        when(bookingMapper.toResponse(savedBooking)).thenReturn(expectedResponse);

        BookingResponse result = bookingService.createBooking(customerId, request);

        assertThat(result).isNotNull();
        assertThat(availableSlot.getStatus()).isEqualTo(SlotStatus.BOOKED);
        verify(eventPublisher).publishEvent(any());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should throw SlotNotAvailableException when slot is already booked")
    void createBooking_shouldThrowConflict_whenSlotAlreadyBooked() {
        availableSlot.setStatus(SlotStatus.BOOKED);
        BookingRequest request = new BookingRequest();
        request.setSlotId(slotId);

        when(userRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(slotRepository.findByIdWithPessimisticLock(slotId)).thenReturn(Optional.of(availableSlot));

        assertThatThrownBy(() -> bookingService.createBooking(customerId, request))
                .isInstanceOf(SlotNotAvailableException.class)
                .hasMessageContaining("already booked");

        verify(bookingRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when slot does not exist")
    void createBooking_shouldThrowNotFound_whenSlotMissing() {
        BookingRequest request = new BookingRequest();
        request.setSlotId(UUID.randomUUID());

        when(userRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(slotRepository.findByIdWithPessimisticLock(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(customerId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should cancel booking and make slot available again")
    void cancelBooking_shouldCancelAndFreeSlot() {
        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setSlot(availableSlot);
        availableSlot.setStatus(SlotStatus.BOOKED);
        booking.setCustomer(customer);
        booking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(slotRepository.save(any())).thenReturn(availableSlot);

        bookingService.cancelBooking(customerId, bookingId);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(availableSlot.getStatus()).isEqualTo(SlotStatus.AVAILABLE);
        verify(eventPublisher).publishEvent(any());
        verify(waitlistService).promoteNextInLine(slotId);
    }
}
