package com.slotsync.backend.controller;

import com.slotsync.backend.dto.request.UpdateProviderProfileRequest;
import com.slotsync.backend.dto.response.BookingResponse;
import com.slotsync.backend.dto.response.ProviderDashboardStats;
import com.slotsync.backend.dto.response.ProviderProfileResponse;
import com.slotsync.backend.security.CustomUserDetails;
import com.slotsync.backend.service.BookingService;
import com.slotsync.backend.service.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;
    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<Page<ProviderProfileResponse>> searchProviders(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        return ResponseEntity
                .ok(providerService.searchProviders(search, category, location, PageRequest.of(page, size, sort)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProviderProfileResponse> getProvider(@PathVariable UUID id) {
        return ResponseEntity.ok(providerService.getProviderById(id));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ProviderProfileResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateProviderProfileRequest request) {
        return ResponseEntity.ok(providerService.updateProvider(userDetails.getId(), request));
    }

    @GetMapping("/{id}/dashboard")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<ProviderDashboardStats> getDashboard(@PathVariable UUID id) {
        return ResponseEntity.ok(providerService.getDashboardStats(id));
    }

    @GetMapping("/{id}/bookings")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponse>> getProviderBookings(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id) {

        return ResponseEntity.ok(bookingService.getBookingsByCustomer(userDetails.getId()));
    }
}
