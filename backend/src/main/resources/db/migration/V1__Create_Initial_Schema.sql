-- Flyway migration v1: Create initial Schema (H2 compatible)

CREATE TABLE users (
    id UUID NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(50) NULL,
    avatar_url VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE provider_profiles (
    id UUID NOT NULL,
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description CLOB NULL,
    category VARCHAR(50) NOT NULL,
    location VARCHAR(255) NULL,
    timezone VARCHAR(100) NOT NULL,
    vacation_mode BOOLEAN NOT NULL DEFAULT FALSE,
    vacation_start DATE NULL,
    vacation_end DATE NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_provider_profiles PRIMARY KEY (id),
    CONSTRAINT uk_provider_profiles_user UNIQUE (user_id),
    CONSTRAINT fk_provider_profiles_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE slots (
    id UUID NOT NULL,
    provider_profile_id UUID NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    duration_minutes INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    title VARCHAR(255) NULL,
    notes CLOB NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_slots PRIMARY KEY (id),
    CONSTRAINT fk_slots_provider_profiles FOREIGN KEY (provider_profile_id) REFERENCES provider_profiles (id) ON DELETE CASCADE
);

CREATE TABLE bookings (
    id UUID NOT NULL,
    slot_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    notes CLOB NULL,
    booked_at TIMESTAMP NOT NULL,
    cancelled_at TIMESTAMP NULL,
    cancellation_reason VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_bookings PRIMARY KEY (id),
    CONSTRAINT uk_bookings_slot UNIQUE (slot_id),
    CONSTRAINT fk_bookings_slots FOREIGN KEY (slot_id) REFERENCES slots (id),
    CONSTRAINT fk_bookings_users FOREIGN KEY (customer_id) REFERENCES users (id)
);

CREATE TABLE waitlist (
    id UUID NOT NULL,
    slot_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    position INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    promoted_at TIMESTAMP NULL,
    expires_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_waitlist PRIMARY KEY (id),
    CONSTRAINT fk_waitlist_slots FOREIGN KEY (slot_id) REFERENCES slots (id) ON DELETE CASCADE,
    CONSTRAINT fk_waitlist_users FOREIGN KEY (customer_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE notifications (
    id UUID NOT NULL,
    recipient_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message CLOB NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    email_sent BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_notifications PRIMARY KEY (id),
    CONSTRAINT fk_notifications_users FOREIGN KEY (recipient_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE refresh_tokens (
    id UUID NOT NULL,
    user_id UUID NOT NULL,
    token VARCHAR(500) NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT uk_refresh_tokens_token UNIQUE (token),
    CONSTRAINT fk_refresh_tokens_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE audit_logs (
    id UUID NOT NULL,
    actor_id UUID NULL,
    action VARCHAR(255) NOT NULL,
    entity_type VARCHAR(100) NULL,
    entity_id UUID NULL,
    details CLOB NULL,
    ip_address VARCHAR(45) NULL,
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT pk_audit_logs PRIMARY KEY (id)
);

-- INDEXES
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_provider_profiles_user ON provider_profiles (user_id);
CREATE INDEX idx_slots_provider_status ON slots (provider_profile_id, status);
CREATE INDEX idx_slots_start_end ON slots (start_time, end_time);
CREATE INDEX idx_bookings_slot ON bookings (slot_id);
CREATE INDEX idx_bookings_customer ON bookings (customer_id);
CREATE INDEX idx_waitlist_slot_pos ON waitlist (slot_id, position, status);
CREATE INDEX idx_waitlist_customer ON waitlist (customer_id);
CREATE INDEX idx_notifications_recipient ON notifications (recipient_id, is_read);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens (token);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens (user_id);
CREATE INDEX idx_audit_logs_actor_time ON audit_logs (actor_id, timestamp);
CREATE INDEX idx_audit_logs_entity ON audit_logs (entity_type, entity_id);
