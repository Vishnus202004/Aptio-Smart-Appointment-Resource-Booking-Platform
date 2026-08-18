package com.slotsync.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Centralizes all SlotSync custom configuration properties.
 *
 * <p>Design Decision: Rather than scattering {@code @Value} annotations across
 * service classes, we bind properties here and expose typed beans. This makes
 * values easily mockable in tests and clearly documents what is configurable.
 */
@Configuration
public class AppConfig {

    // ── JWT ──────────────────────────────────────────────────────────────────
    @Value("${slotsync.jwt.secret}")
    private String jwtSecret;

    @Value("${slotsync.jwt.access-token-expiry-ms}")
    private long accessTokenExpiryMs;

    @Value("${slotsync.jwt.refresh-token-expiry-ms}")
    private long refreshTokenExpiryMs;

    // ── Booking ──────────────────────────────────────────────────────────────
    @Value("${slotsync.booking.lock-timeout-seconds:10}")
    private int lockTimeoutSeconds;

    // ── Notifications ────────────────────────────────────────────────────────
    @Value("${slotsync.notifications.email.enabled:false}")
    private boolean emailNotificationsEnabled;

    // ── Expose as records / beans ─────────────────────────────────────────────

    @Bean
    public JwtProperties jwtProperties() {
        return new JwtProperties(jwtSecret, accessTokenExpiryMs, refreshTokenExpiryMs);
    }

    @Bean
    public BookingProperties bookingProperties() {
        return new BookingProperties(lockTimeoutSeconds);
    }

    @Bean
    public NotificationProperties notificationProperties() {
        return new NotificationProperties(emailNotificationsEnabled);
    }

    // ── Inner Records ────────────────────────────────────────────────────────

    /**
     * JWT configuration record.
     *
     * @param secret           HMAC-SHA256 signing key (min 256-bit / 32 chars)
     * @param accessExpiryMs   Access token TTL in milliseconds (default: 15 min)
     * @param refreshExpiryMs  Refresh token TTL in milliseconds (default: 7 days)
     */
    public record JwtProperties(String secret, long accessExpiryMs, long refreshExpiryMs) {}

    /**
     * Booking engine configuration record.
     *
     * @param lockTimeoutSeconds How long to wait for a pessimistic DB lock
     */
    public record BookingProperties(int lockTimeoutSeconds) {}

    /**
     * Notification feature flags.
     *
     * @param emailEnabled  Whether SMTP email sending is active.
     *                      If false, the app functions without a mail server.
     */
    public record NotificationProperties(boolean emailEnabled) {}
}
