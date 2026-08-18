package com.slotsync.backend.service.impl;

import com.slotsync.backend.domain.AuditLog;
import com.slotsync.backend.domain.enums.BookingStatus;
import com.slotsync.backend.domain.enums.Role;
import com.slotsync.backend.domain.enums.SlotStatus;
import com.slotsync.backend.domain.enums.UserStatus;
import com.slotsync.backend.dto.response.AdminDashboardStats;
import com.slotsync.backend.dto.response.BookingResponse;
import com.slotsync.backend.dto.response.UserResponse;
import com.slotsync.backend.exception.ResourceNotFoundException;
import com.slotsync.backend.mapper.BookingMapper;
import com.slotsync.backend.mapper.UserMapper;
import com.slotsync.backend.repository.BookingRepository;
import com.slotsync.backend.repository.ProviderProfileRepository;
import com.slotsync.backend.repository.SlotRepository;
import com.slotsync.backend.repository.UserRepository;
import com.slotsync.backend.service.AdminService;
import com.slotsync.backend.service.AuditLogService;
import com.slotsync.backend.specification.BookingSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final SlotRepository slotRepository;
    private final AuditLogService auditLogService;
    private final UserMapper userMapper;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(String search, Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    @Override
    @Transactional
    public void updateUserStatus(UUID userId, UserStatus status) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus(status);
        userRepository.save(user);
        auditLogService.logAction(null, "USER_STATUS_CHANGED_TO_" + status, "USER", userId, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponse> getAllBookings(String status, Pageable pageable) {
        BookingStatus bookingStatus = null;
        if (status != null && !status.isBlank()) {
            try { bookingStatus = BookingStatus.valueOf(status.toUpperCase()); } catch (IllegalArgumentException ignored) {}
        }
        Specification<com.slotsync.backend.domain.Booking> spec = BookingSpecification.hasStatus(bookingStatus);
        return bookingRepository.findAll(spec, pageable).map(bookingMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return auditLogService.getAuditLogs(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardStats getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalProviders = providerProfileRepository.count();
        long totalBookings = bookingRepository.count();
        long activeBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED).count();
        long cancelledBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.CANCELLED).count();
        long totalSlots = slotRepository.count();
        long availableSlots = slotRepository.findAll().stream()
                .filter(s -> s.getStatus() == SlotStatus.AVAILABLE).count();

        return AdminDashboardStats.builder()
                .totalUsers(totalUsers)
                .totalProviders(totalProviders)
                .totalBookings(totalBookings)
                .totalActiveBookings(activeBookings)
                .totalCancelledBookings(cancelledBookings)
                .totalSlots(totalSlots)
                .totalAvailableSlots(availableSlots)
                .build();
    }
}
