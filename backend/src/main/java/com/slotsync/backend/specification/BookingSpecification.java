package com.slotsync.backend.specification;

import com.slotsync.backend.domain.Booking;
import com.slotsync.backend.domain.enums.BookingStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class BookingSpecification {

    public static Specification<Booking> hasCustomerId(UUID customerId) {
        return (root, query, cb) -> customerId == null ? cb.conjunction() : cb.equal(root.get("customer").get("id"), customerId);
    }

    public static Specification<Booking> hasProviderProfileId(UUID providerId) {
        return (root, query, cb) -> providerId == null ? cb.conjunction() : cb.equal(root.get("slot").get("providerProfile").get("id"), providerId);
    }

    public static Specification<Booking> hasStatus(BookingStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }
}
