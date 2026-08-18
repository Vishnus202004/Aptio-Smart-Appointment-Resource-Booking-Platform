package com.slotsync.backend.config;

import com.slotsync.backend.domain.ProviderProfile;
import com.slotsync.backend.domain.Slot;
import com.slotsync.backend.domain.User;
import com.slotsync.backend.domain.enums.ProviderCategory;
import com.slotsync.backend.domain.enums.Role;
import com.slotsync.backend.domain.enums.SlotStatus;
import com.slotsync.backend.domain.enums.UserStatus;
import com.slotsync.backend.repository.ProviderProfileRepository;
import com.slotsync.backend.repository.SlotRepository;
import com.slotsync.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final SlotRepository slotRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Database already has data — skipping seeding.");
            return;
        }

        log.info("Seeding initial demo data...");

        String password = passwordEncoder.encode("password123");

        // --- Admin ---
        User admin = new User();
        admin.setEmail("admin@slotsync.io");
        admin.setPasswordHash(password);
        admin.setFirstName("System");
        admin.setLastName("Administrator");
        admin.setRole(Role.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);
        userRepository.save(admin);

        // --- Provider user + profile ---
        User providerUser = new User();
        providerUser.setEmail("provider@slotsync.io");
        providerUser.setPasswordHash(password);
        providerUser.setFirstName("Dr. Sarah");
        providerUser.setLastName("Connor");
        providerUser.setRole(Role.PROVIDER);
        providerUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(providerUser);

        ProviderProfile profile = new ProviderProfile();
        profile.setUser(providerUser);
        profile.setName("Dr. Sarah Connor — Healthcare Clinic");
        profile.setDescription("Experienced general practitioner offering consultation appointments and health check-ups.");
        profile.setCategory(ProviderCategory.HEALTHCARE);
        profile.setLocation("San Francisco, CA");
        profile.setTimezone("America/Los_Angeles");
        providerProfileRepository.save(profile);

        // --- Provider 2 user + profile ---
        User consultUser = new User();
        consultUser.setEmail("consult@slotsync.io");
        consultUser.setPasswordHash(password);
        consultUser.setFirstName("Alex");
        consultUser.setLastName("Rivera");
        consultUser.setRole(Role.PROVIDER);
        consultUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(consultUser);

        ProviderProfile profile2 = new ProviderProfile();
        profile2.setUser(consultUser);
        profile2.setName("Alex Rivera — Business Consulting");
        profile2.setDescription("Strategic business consulting for startups and enterprises. Book a 1-on-1 strategy session.");
        profile2.setCategory(ProviderCategory.CONSULTING);
        profile2.setLocation("New York, NY");
        profile2.setTimezone("America/New_York");
        providerProfileRepository.save(profile2);

        // --- Customer ---
        User customer = new User();
        customer.setEmail("customer@slotsync.io");
        customer.setPasswordHash(password);
        customer.setFirstName("Vishnu");
        customer.setLastName("Demo");
        customer.setRole(Role.CUSTOMER);
        customer.setStatus(UserStatus.ACTIVE);
        userRepository.save(customer);

        // --- Create slots for provider 1 ---
        Instant now = Instant.now().truncatedTo(ChronoUnit.HOURS).plus(1, ChronoUnit.HOURS);
        String[] slotTitles = {"Morning Checkup", "Afternoon Consultation", "Follow-up Session", "General Checkup", "Blood Test Review"};
        for (int i = 0; i < slotTitles.length; i++) {
            Slot slot = new Slot();
            slot.setProviderProfile(profile);
            slot.setStartTime(now.plus(i + 1, ChronoUnit.DAYS));
            slot.setEndTime(now.plus(i + 1, ChronoUnit.DAYS).plus(60, ChronoUnit.MINUTES));
            slot.setDurationMinutes(60);
            slot.setStatus(SlotStatus.AVAILABLE);
            slot.setTitle(slotTitles[i]);
            slot.setNotes("Please arrive 10 minutes early.");
            slotRepository.save(slot);
        }

        // --- Create slots for provider 2 ---
        String[] slotTitles2 = {"Strategy Session", "Growth Consulting", "Market Analysis", "Pitch Review"};
        for (int i = 0; i < slotTitles2.length; i++) {
            Slot slot = new Slot();
            slot.setProviderProfile(profile2);
            slot.setStartTime(now.plus(i + 1, ChronoUnit.DAYS).plus(3, ChronoUnit.HOURS));
            slot.setEndTime(now.plus(i + 1, ChronoUnit.DAYS).plus(3, ChronoUnit.HOURS).plus(90, ChronoUnit.MINUTES));
            slot.setDurationMinutes(90);
            slot.setStatus(SlotStatus.AVAILABLE);
            slot.setTitle(slotTitles2[i]);
            slotRepository.save(slot);
        }

        log.info("Seed data complete! Login credentials:");
        log.info("  Admin:    admin@slotsync.io / password123");
        log.info("  Provider: provider@slotsync.io / password123");
        log.info("  Customer: customer@slotsync.io / password123");
    }
}
