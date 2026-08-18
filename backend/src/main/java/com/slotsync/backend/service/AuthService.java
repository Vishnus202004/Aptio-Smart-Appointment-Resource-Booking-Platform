package com.slotsync.backend.service;

import com.slotsync.backend.dto.request.LoginRequest;
import com.slotsync.backend.dto.request.RegisterRequest;
import com.slotsync.backend.dto.request.TokenRefreshRequest;
import com.slotsync.backend.dto.response.AuthResponse;
import com.slotsync.backend.dto.response.TokenRefreshResponse;

import java.util.UUID;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    TokenRefreshResponse refresh(TokenRefreshRequest request);
    void logout(String refreshTokenValue);
    
    // Structure only for future expansion
    void verifyEmail(UUID userId, String token);
    void initiatePasswordReset(String email);
    void completePasswordReset(String token, String newPassword);
}
