package com.slotsync.backend.controller;

import com.slotsync.backend.dto.request.LoginRequest;
import com.slotsync.backend.dto.request.RegisterRequest;
import com.slotsync.backend.dto.request.TokenRefreshRequest;
import com.slotsync.backend.dto.response.AuthResponse;
import com.slotsync.backend.dto.response.TokenRefreshResponse;
import com.slotsync.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam("token") String token) {
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }
}
