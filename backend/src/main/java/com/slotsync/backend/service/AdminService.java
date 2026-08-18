package com.slotsync.backend.service;

import com.slotsync.backend.domain.AuditLog;
import com.slotsync.backend.domain.enums.UserStatus;
import com.slotsync.backend.dto.response.AdminDashboardStats;
import com.slotsync.backend.dto.response.BookingResponse;
import com.slotsync.backend.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AdminService {
    Page<UserResponse> getAllUsers(String search, Pageable pageable);
    void updateUserStatus(UUID userId, UserStatus status);
    Page<BookingResponse> getAllBookings(String status, Pageable pageable);
    Page<AuditLog> getAuditLogs(Pageable pageable);
    AdminDashboardStats getDashboardStats();
}
