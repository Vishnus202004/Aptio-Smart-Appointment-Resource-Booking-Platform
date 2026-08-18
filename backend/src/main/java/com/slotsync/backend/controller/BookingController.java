package com.slotsync.backend.controller;

import com.slotsync.backend.dto.request.BookingRequest;
import com.slotsync.backend.dto.response.BookingResponse;
import com.slotsync.backend.dto.response.WaitlistResponse;
import com.slotsync.backend.security.CustomUserDetails;
import com.slotsync.backend.service.BookingService;
import com.slotsync.backend.service.WaitlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final WaitlistService waitlistService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponse> createBooking(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody BookingRequest request) {
        return new ResponseEntity<>(bookingService.createBooking(userDetails.getId(), request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponse>> getMyBookings(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(bookingService.getBookingsByCustomer(userDetails.getId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Void> cancelBooking(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") UUID bookingId) {
        bookingService.cancelBooking(userDetails.getId(), bookingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/slots/{slotId}/waitlist")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<WaitlistResponse> joinWaitlist(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("slotId") UUID slotId) {
        return new ResponseEntity<>(waitlistService.joinWaitlist(userDetails.getId(), slotId), HttpStatus.CREATED);
    }
}
