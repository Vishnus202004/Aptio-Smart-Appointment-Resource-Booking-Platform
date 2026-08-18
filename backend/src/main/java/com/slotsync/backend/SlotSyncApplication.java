package com.slotsync.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SlotSync Backend Application Entry Point.
 *
 * <p>Key capabilities enabled at startup:
 * <ul>
 *   <li>{@code @EnableAsync} — powers the {@code @Async} event listeners used
 *       in the notification pipeline, so domain events do not block the
 *       booking transaction thread.</li>
 *   <li>{@code @EnableScheduling} — activates the recurring slot generation
 *       scheduler and any maintenance tasks (e.g., expired waitlist cleanup).</li>
 *   <li>{@code @EnableCaching} — caches frequently read, rarely mutated data
 *       such as provider listings and slot availability summaries.</li>
 * </ul>
 *
 * <p>All configuration is driven by {@code application.yml} and the active
 * Spring profile ({@code dev}, {@code test}, or {@code prod}).
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableCaching
public class SlotSyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(SlotSyncApplication.class, args);
    }
}
