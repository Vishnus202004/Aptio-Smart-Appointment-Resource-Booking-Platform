package com.slotsync.backend.service.impl;

import com.slotsync.backend.config.AppConfig.JwtProperties;
import com.slotsync.backend.domain.ProviderProfile;
import com.slotsync.backend.domain.RefreshToken;
import com.slotsync.backend.domain.User;
import com.slotsync.backend.domain.enums.Role;
import com.slotsync.backend.domain.enums.UserStatus;
import com.slotsync.backend.dto.request.LoginRequest;
import com.slotsync.backend.dto.request.RegisterRequest;
import com.slotsync.backend.dto.request.TokenRefreshRequest;
import com.slotsync.backend.dto.response.AuthResponse;
import com.slotsync.backend.dto.response.TokenRefreshResponse;
import com.slotsync.backend.exception.BadRequestException;
import com.slotsync.backend.exception.UnauthorizedException;
import com.slotsync.backend.mapper.UserMapper;
import com.slotsync.backend.repository.ProviderProfileRepository;
import com.slotsync.backend.repository.RefreshTokenRepository;
import com.slotsync.backend.repository.UserRepository;
import com.slotsync.backend.security.CustomUserDetails;
import com.slotsync.backend.security.JwtTokenProvider;
import com.slotsync.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus(UserStatus.ACTIVE); // Auto-active for prototype
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());

        User savedUser = userRepository.save(user);

        // Auto-create profile if user is a provider
        if (savedUser.getRole() == Role.PROVIDER) {
            ProviderProfile profile = new ProviderProfile();
            profile.setUser(savedUser);
            profile.setName(savedUser.getFirstName() + " " + savedUser.getLastName());
            profile.setTimezone("UTC");
            profile.setCategory(com.slotsync.backend.domain.enums.ProviderCategory.OTHER);
            providerProfileRepository.save(profile);
        }

        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        RefreshToken refreshToken = createRefreshToken(savedUser);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(userMapper.toResponse(savedUser))
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new UnauthorizedException("Your account is suspended");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);

        // Delete old refresh tokens on login
        refreshTokenRepository.deleteByUserId(user.getId());
        RefreshToken refreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(userMapper.toResponse(user))
                .build();
    }

    @Override
    @Transactional
    public TokenRefreshResponse refresh(TokenRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Expired or revoked refresh token");
        }

        // Refresh token rotation: generate a new refresh token and delete the old one
        User user = refreshToken.getUser();
        refreshTokenRepository.delete(refreshToken);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
        RefreshToken newRefreshToken = createRefreshToken(user);

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshTokenValue) {
        refreshTokenRepository.findByToken(refreshTokenValue)
                .ifPresent(refreshTokenRepository::delete);
    }

    @Override
    public void verifyEmail(UUID userId, String token) {
        // Struct only
    }

    @Override
    public void initiatePasswordReset(String email) {
        // Struct only
    }

    @Override
    public void completePasswordReset(String token, String newPassword) {
        // Struct only
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(Instant.now().plusMillis(jwtProperties.refreshExpiryMs()));
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }
}
