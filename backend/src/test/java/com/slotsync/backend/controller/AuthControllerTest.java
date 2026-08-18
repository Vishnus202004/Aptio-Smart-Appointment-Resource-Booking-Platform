package com.slotsync.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slotsync.backend.config.AppConfig;
import com.slotsync.backend.config.AppConfig.JwtProperties;
import com.slotsync.backend.domain.enums.Role;
import com.slotsync.backend.dto.request.LoginRequest;
import com.slotsync.backend.dto.request.RegisterRequest;
import com.slotsync.backend.dto.response.AuthResponse;
import com.slotsync.backend.dto.response.UserResponse;
import com.slotsync.backend.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("AuthController MockMvc Tests")
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private AuthService authService;

    private final UserResponse mockUser = UserResponse.builder()
            .id(UUID.randomUUID())
            .email("test@slotsync.io")
            .role("CUSTOMER")
            .firstName("Test")
            .lastName("User")
            .build();

    @Test
    @DisplayName("POST /api/v1/auth/register - 201 Created")
    void register_shouldReturn201() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@slotsync.io");
        req.setPassword("password123");
        req.setRole(Role.CUSTOMER);
        req.setFirstName("Test");
        req.setLastName("User");

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("access.token")
                .refreshToken("refresh.token")
                .user(mockUser)
                .build();

        when(authService.register(any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access.token"))
                .andExpect(jsonPath("$.user.email").value("test@slotsync.io"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - 400 Bad Request on invalid input")
    void register_shouldReturn400_onInvalidInput() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("not-an-email");
        req.setPassword("123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - 200 OK")
    void login_shouldReturn200() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@slotsync.io");
        req.setPassword("password123");

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("access.token")
                .refreshToken("refresh.token")
                .user(mockUser)
                .build();

        when(authService.login(any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access.token"));
    }
}
