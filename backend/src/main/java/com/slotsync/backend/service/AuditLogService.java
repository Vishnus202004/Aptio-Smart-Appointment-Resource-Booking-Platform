package com.slotsync.backend.service;

import com.slotsync.backend.domain.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AuditLogService {
    void logAction(UUID actorId, String action, String entityType, UUID entityId, String details, String ipAddress);
    Page<AuditLog> getAuditLogs(Pageable pageable);
}
