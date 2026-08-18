package com.slotsync.backend.domain.enums;

/**
 * User roles for RBAC.
 * Stored as strings in DB for readability and migration safety.
 */
public enum Role {
    ADMIN,
    PROVIDER,
    CUSTOMER
}
