# SlotSync — Entity Relationship Diagram

## ER Diagram

```mermaid
erDiagram
    USER {
        bigint      id              PK
        varchar     email           UK
        varchar     password_hash
        enum        role            "ADMIN|PROVIDER|CUSTOMER"
        enum        status          "ACTIVE|SUSPENDED|DELETED"
        varchar     first_name
        varchar     last_name
        varchar     phone
        varchar     avatar_url
        timestamp   created_at
        timestamp   updated_at
    }

    PROVIDER {
        bigint      id              PK
        bigint      user_id         FK,UK
        varchar     name
        text        description
        varchar     category        "HEALTHCARE|COWORKING|CONSULTING|EVENTS|OTHER"
        varchar     location
        varchar     timezone
        boolean     vacation_mode
        date        vacation_start
        date        vacation_end
        float       rating
        int         total_bookings
        timestamp   created_at
        timestamp   updated_at
    }

    SLOT {
        bigint      id              PK
        bigint      provider_id     FK
        timestamp   start_time
        timestamp   end_time
        int         duration_minutes
        enum        status          "AVAILABLE|BOOKED|BLOCKED|CANCELLED"
        varchar     title
        text        notes
        integer     version         "Optimistic lock @Version"
        timestamp   created_at
        timestamp   updated_at
    }

    BOOKING {
        bigint      id              PK
        bigint      slot_id         FK,UK
        bigint      customer_id     FK
        enum        status          "CONFIRMED|CANCELLED|COMPLETED|NO_SHOW"
        text        notes
        timestamp   booked_at
        timestamp   cancelled_at
        varchar     cancellation_reason
        timestamp   created_at
        timestamp   updated_at
    }

    WAITLIST {
        bigint      id              PK
        bigint      slot_id         FK
        bigint      customer_id     FK
        int         position        "FIFO order"
        enum        status          "WAITING|PROMOTED|EXPIRED|CANCELLED"
        timestamp   joined_at
        timestamp   promoted_at
        timestamp   expires_at
    }

    NOTIFICATION {
        bigint      id              PK
        bigint      recipient_id    FK
        enum        type            "BOOKING_CONFIRMED|BOOKING_CANCELLED|WAITLIST_PROMOTED|REMINDER"
        varchar     title
        text        message
        boolean     is_read
        boolean     email_sent
        timestamp   created_at
        timestamp   read_at
    }

    REFRESH_TOKEN {
        bigint      id              PK
        bigint      user_id         FK
        varchar     token           UK
        boolean     revoked
        timestamp   expires_at
        timestamp   created_at
    }

    AUDIT_LOG {
        bigint      id              PK
        bigint      actor_id        FK
        varchar     action          "USER_LOGIN|SLOT_CREATED|BOOKING_CONFIRMED|..."
        varchar     entity_type     "USER|SLOT|BOOKING|PROVIDER"
        bigint      entity_id
        text        details         "JSON details of the change"
        varchar     ip_address
        timestamp   timestamp
    }

    %% ── Relationships ────────────────────────────────────────────────────
    USER         ||--o|  PROVIDER        : "is a"
    PROVIDER     ||--o{  SLOT            : "offers"
    SLOT         ||--o|  BOOKING         : "has"
    USER         ||--o{  BOOKING         : "makes"
    SLOT         ||--o{  WAITLIST        : "has"
    USER         ||--o{  WAITLIST        : "joins"
    USER         ||--o{  NOTIFICATION    : "receives"
    USER         ||--o{  REFRESH_TOKEN   : "has"
    USER         ||--o{  AUDIT_LOG       : "triggers"
```

---

## Entity Design Decisions

### `Slot.version` — Optimistic Locking
The `version` column is annotated with `@Version` in JPA. On every `UPDATE`, Hibernate appends `WHERE version = :expected` to the query. If two concurrent transactions update the same row, the second one fails with `OptimisticLockException`, which the service layer translates to `HTTP 409 Conflict`.

**Switching to Pessimistic Locking**: `SlotRepository.findByIdWithLock()` uses `@Lock(LockModeType.PESSIMISTIC_WRITE)` which issues `SELECT ... FOR UPDATE`. This is activated in high-concurrency scenarios by calling the lock-specific repository method instead of the standard `findById`.

### `Booking` ↔ `Slot` — One-to-One with Unique Constraint
The `slot_id` column on `Booking` has a `UNIQUE` constraint at the database level. This is the **second line of defense** against double booking (after the optimistic/pessimistic lock). Even if the application code has a race condition, the database constraint guarantees integrity.

### `Waitlist.position` — FIFO Ordering
`position` is assigned as `MAX(position) + 1` within the same slot. When a booking is cancelled, the waitlist entry with the lowest `position` and status `WAITING` is promoted atomically in a single transaction.

### `AuditLog.details` — JSON String
The details column stores a serialized JSON snapshot of what changed. This trades strict normalization for query simplicity — audit logs are append-only and rarely queried with complex joins.

---

## Indexes

```sql
-- Slot availability queries (most frequent read pattern)
CREATE INDEX idx_slot_provider_status    ON slot (provider_id, status);
CREATE INDEX idx_slot_provider_starttime ON slot (provider_id, start_time);

-- Booking history for a customer
CREATE INDEX idx_booking_customer        ON booking (customer_id, status);

-- Waitlist FIFO lookup
CREATE INDEX idx_waitlist_slot_position  ON waitlist (slot_id, position, status);

-- Notification feed
CREATE INDEX idx_notification_recipient  ON notification (recipient_id, is_read, created_at DESC);

-- Audit log queries
CREATE INDEX idx_audit_actor_time        ON audit_log (actor_id, timestamp DESC);
CREATE INDEX idx_audit_entity            ON audit_log (entity_type, entity_id);
```
