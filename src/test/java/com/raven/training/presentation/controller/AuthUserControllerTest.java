package com.raven.training.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raven.training.presentation.dto.login.AuthLoginRequest;
import com.raven.training.presentation.dto.login.AuthLoginResponse;
import com.raven.training.presentation.dto.register.AuthRegisterRequest;
import com.raven.training.presentation.dto.register.AuthRegisterResponse;
import com.raven.training.service.implementation.UserDetailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AuthUserController.class)
@AutoConfigureMockMvc
@DisplayName("Unit tests for AuthUserController")
class AuthUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserDetailServiceImpl userDetailService;

    @InjectMocks
    private AuthUserController authUserController;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private AuthLoginRequest authLoginRequest;
    private AuthRegisterRequest authRegisterRequest;
    private AuthLoginResponse authLoginResponse;
    private AuthRegisterResponse authRegisterResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        authLoginRequest = new AuthLoginRequest("testuser", "password123");


        authLoginResponse = new AuthLoginResponse(
                "testuser",
                "Login exitoso",
                "jwt.token.here",
                true
        );

        authRegisterRequest = new AuthRegisterRequest(
                "testuser",
                "password123",
                "test@example.com"
        );

        authRegisterResponse = new AuthRegisterResponse(
                "testuser",
                "test@example.com",
                "User successfully registered",
                true
        );
    }

    @Test
    @DisplayName("Should authenticate a user successfully")
    void login_ShouldReturnAuthToken_WhenCredentialsAreValid() throws Exception {
        when(userDetailService.loginUser(any(AuthLoginRequest.class))).thenReturn(authLoginResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authLoginRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(authLoginResponse.username()))
                .andExpect(jsonPath("$.jwt").value(authLoginResponse.jwt()));

        verify(userDetailService, times(1)).loginUser(any(AuthLoginRequest.class));
    }

    @Test
    @DisplayName("You should register a new user successfully")
    void register_ShouldReturnUserDetails_WhenRegistrationIsSuccessful() throws Exception {
        when(userDetailService.registerUser(any(AuthRegisterRequest.class))).thenReturn(authRegisterResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRegisterRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(authRegisterResponse.username()))
                .andExpect(jsonPath("$.email").value(authRegisterResponse.email()));

        verify(userDetailService, times(1)).registerUser(any(AuthRegisterRequest.class));
    }
}