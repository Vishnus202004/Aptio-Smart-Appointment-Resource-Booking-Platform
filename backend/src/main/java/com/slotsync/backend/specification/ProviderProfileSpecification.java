package com.slotsync.backend.specification;

import com.slotsync.backend.domain.ProviderProfile;
import com.slotsync.backend.domain.enums.ProviderCategory;
import org.springframework.data.jpa.domain.Specification;

public class ProviderProfileSpecification {

    public static Specification<ProviderProfile> hasCategory(ProviderCategory category) {
        return (root, query, cb) -> category == null ? cb.conjunction() : cb.equal(root.get("category"), category);
    }

    public static Specification<ProviderProfile> searchByNameOrDescription(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + searchTerm.trim().toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<ProviderProfile> hasLocation(String location) {
        return (root, query, cb) -> {
            if (location == null || location.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("location")), "%" + location.trim().toLowerCase() + "%");
        };
    }
}
