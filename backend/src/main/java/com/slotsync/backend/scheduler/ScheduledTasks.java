package com.slotsync.backend.scheduler;

import com.slotsync.backend.domain.enums.WaitlistStatus;
import com.slotsync.backend.repository.WaitlistRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private final WaitlistRepository waitlistRepository;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredWaitlistEntries() {
        log.info("Running scheduled cleanup for expired waitlist entries...");
        waitlistRepository.findAll().stream()
                .filter(w -> w.getStatus() == WaitlistStatus.WAITING && w.getExpiresAt() != null
                        && w.getExpiresAt().isBefore(Instant.now()))
                .forEach(w -> {
                    w.setStatus(WaitlistStatus.EXPIRED);
                    waitlistRepository.save(w);
                    log.info("Expired waitlist entry ID {}", w.getId());
                });
    }
}
