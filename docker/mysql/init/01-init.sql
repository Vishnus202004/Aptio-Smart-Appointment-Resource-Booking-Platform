-- Seed Data SQL script to populate database profiles and initial mock users
-- User Passwords are "password123" encrypted with BCrypt ($2a$10$wU05Zpve4KzG4y7h2w.LReb7L8H8wQkP2d1l3GZ/T7cR8g2hJ/S0i)

-- Insert Admin
INSERT INTO users (id, email, password_hash, role, status, first_name, last_name, created_at, updated_at)
VALUES (
    UNHEX(REPLACE('a1111111-1111-1111-1111-111111111111', '-', '')),
    'admin@slotsync.io',
    '$2a$10$wU05Zpve4KzG4y7h2w.LReb7L8H8wQkP2d1l3GZ/T7cR8g2hJ/S0i',
    'ADMIN',
    'ACTIVE',
    'System',
    'Administrator',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE email=email;

-- Insert Provider
INSERT INTO users (id, email, password_hash, role, status, first_name, last_name, created_at, updated_at)
VALUES (
    UNHEX(REPLACE('b2222222-2222-2222-2222-222222222222', '-', '')),
    'provider@slotsync.io',
    '$2a$10$wU05Zpve4KzG4y7h2w.LReb7L8H8wQkP2d1l3GZ/T7cR8g2hJ/S0i',
    'PROVIDER',
    'ACTIVE',
    'John',
    'Doe',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE email=email;

-- Insert Provider Profile
INSERT INTO provider_profiles (id, user_id, name, description, category, location, timezone, created_at, updated_at)
VALUES (
    UNHEX(REPLACE('c3333333-3333-3333-3333-333333333333', '-', '')),
    UNHEX(REPLACE('b2222222-2222-2222-2222-222222222222', '-', '')),
    'Dr. John Doe',
    'Expert consultation clinic providing health checkups and advice.',
    'HEALTHCARE',
    'San Francisco, CA',
    'America/Los_Angeles',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE name=name;

-- Insert Customer
INSERT INTO users (id, email, password_hash, role, status, first_name, last_name, created_at, updated_at)
VALUES (
    UNHEX(REPLACE('d4444444-4444-4444-4444-444444444444', '-', '')),
    'customer@slotsync.io',
    '$2a$10$wU05Zpve4KzG4y7h2w.LReb7L8H8wQkP2d1l3GZ/T7cR8g2hJ/S0i',
    'CUSTOMER',
    'ACTIVE',
    'Alice',
    'Smith',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE email=email;

-- Insert Initial Available Slots
INSERT INTO slots (id, provider_profile_id, start_time, end_time, duration_minutes, status, title, notes, created_at, updated_at)
VALUES (
    UNHEX(REPLACE('e5555555-5555-5555-5555-555555555555', '-', '')),
    UNHEX(REPLACE('c3333333-3333-3333-3333-333333333333', '-', '')),
    DATE_ADD(NOW(), INTERVAL 1 DAY),
    DATE_ADD(NOW(), INTERVAL 1 DAY) + INTERVAL 1 HOUR,
    60,
    'AVAILABLE',
    'Regular Checkup Session',
    'Bring previous reports.',
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE title=title;
