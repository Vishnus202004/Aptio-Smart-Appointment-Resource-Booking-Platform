package com.slotsync.backend.controller;

import com.slotsync.backend.dto.request.CreateSlotRequest;
import com.slotsync.backend.dto.response.SlotResponse;
import com.slotsync.backend.service.SlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/slots")
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;

    @PostMapping
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<SlotResponse> createSlot(@Valid @RequestBody CreateSlotRequest request) {
        return new ResponseEntity<>(slotService.createSlot(request), HttpStatus.CREATED);
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<SlotResponse>> getProviderSlots(
            @PathVariable("providerId") UUID providerId,
            @RequestParam("start") String startIso,
            @RequestParam("end") String endIso) {
        return ResponseEntity.ok(slotService.getSlotsByProviderAndDateRange(
                providerId, Instant.parse(startIso), Instant.parse(endIso)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSlot(@PathVariable("id") UUID id) {
        slotService.deleteSlot(id);
        return ResponseEntity.noContent().build();
    }
}
