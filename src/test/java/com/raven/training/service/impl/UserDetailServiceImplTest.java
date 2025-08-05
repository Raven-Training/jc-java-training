package com.raven.training.service.impl;

import com.raven.training.persistence.entity.AuthUser;
import com.raven.training.persistence.entity.User;
import com.raven.training.persistence.repository.IAuthUserRepository;
import com.raven.training.persistence.repository.IUserRepository;
import com.raven.training.presentation.dto.login.AuthLoginRequest;
import com.raven.training.presentation.dto.login.AuthLoginResponse;
import com.raven.training.presentation.dto.register.AuthRegisterRequest;
import com.raven.training.presentation.dto.register.AuthRegisterResponse;
import com.raven.training.service.implementation.UserDetailServiceImpl;
import com.raven.training.util.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for UserDetailServiceImpl")
class UserDetailServiceImplTest {

    @Mock
    private IAuthUserRepository authUserRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserDetailServiceImpl userDetailService;

    private AuthUser testUser;
    private final String username = "testuser";
    private final String password = "password123";
    private final String email = "test@example.com";
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        testUser = AuthUser.builder()
                .id(userId)
                .username(username)
                .password("$2a$10$somehashedpassword")
                .email(email)
                .isEnabled(true)
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialNoExpired(true)
                .build();
    }

    @Test
    @DisplayName("Should load a user by username when it exists")
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        when(authUserRepository.findAuthUserByUsername(username))
                .thenReturn(java.util.Optional.of(testUser));

        UserDetails userDetails = userDetailService.loadUserByUsername(username);

        assertNotNull(userDetails, "The UserDetails should not be null");
        assertEquals(username, userDetails.getUsername(), "Username should match");
        assertTrue(userDetails.isEnabled(), "The user should be enabled");
        verify(authUserRepository, times(1)).findAuthUserByUsername(username);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when the user does not exist")
    void loadUserByUsername_WhenUserNotExists_ShouldThrowException() {
        String nonExistentUsername = "nonexistent";
        when(authUserRepository.findAuthUserByUsername(nonExistentUsername))
                .thenReturn(java.util.Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailService.loadUserByUsername(nonExistentUsername),
                "Should throw UsernameNotFoundException");
        verify(authUserRepository, times(1)).findAuthUserByUsername(nonExistentUsername);
    }

    @Test
    @DisplayName("Should authenticate a user with valid credentials")
    void authenticate_WithValidCredentials_ShouldReturnAuthentication() {
        when(authUserRepository.findAuthUserByUsername(username))
                .thenReturn(java.util.Optional.of(testUser));
        when(passwordEncoder.matches(password, testUser.getPassword()))
                .thenReturn(true);

        Authentication authentication = userDetailService.authenticate(username, password);

        assertNotNull(authentication, "Authentication should not be null");
        assertEquals(username, authentication.getName(), "Username should match");
        assertTrue(authentication.getAuthorities().isEmpty(), "There should be no authorities since an empty list is passed around");
        verify(authUserRepository, times(1)).findAuthUserByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, testUser.getPassword());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException with invalid credentials")
    void authenticate_WithInvalidPassword_ShouldThrowException() {
        String wrongPassword = "wrongpassword";
        when(authUserRepository.findAuthUserByUsername(username))
                .thenReturn(java.util.Optional.of(testUser));
        when(passwordEncoder.matches(wrongPassword, testUser.getPassword()))
                .thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> userDetailService.authenticate(username, wrongPassword),
                "Should throw BadCredentialsException");
        verify(authUserRepository, times(1)).findAuthUserByUsername(username);
        verify(passwordEncoder, times(1)).matches(wrongPassword, testUser.getPassword());
    }

    @Test
    @DisplayName("Should be logged in successfully with valid credentials")
    void loginUser_WithValidCredentials_ShouldReturnAuthResponse() {
        AuthLoginRequest loginRequest = new AuthLoginRequest(username, password);
        String expectedToken = "mocked.jwt.token";
        
        when(authUserRepository.findAuthUserByUsername(username))
                .thenReturn(java.util.Optional.of(testUser));
        when(passwordEncoder.matches(password, testUser.getPassword()))
                .thenReturn(true);
        when(jwtUtils.createToken(any(Authentication.class)))
                .thenReturn(expectedToken);

        AuthLoginResponse response = userDetailService.loginUser(loginRequest);

        assertNotNull(response, "The answer should not be null");
        assertEquals(username, response.username(), "Username should match");
        assertEquals(expectedToken, response.jwt(), "The JWT token should match");
        assertTrue(response.status(), "The state should be true");
        verify(authUserRepository, times(1)).findAuthUserByUsername(username);
        verify(jwtUtils, times(1)).createToken(any(Authentication.class));
    }

    @Test
    @DisplayName("Should register a new user correctly with all fields.")
    void registerUser_WithValidData_ShouldReturnAuthRegisterResponse() {
        String rawPassword = "newPassword123";
        String encodedPassword = "$2a$10$encodedpassword";
        String name = "Test User";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        AuthRegisterRequest registerRequest = new AuthRegisterRequest(
                name,
                birthDate,
                "newuser",
                rawPassword,
                "newuser@example.com");

        UUID userId = UUID.randomUUID();

        AuthUser newUser = AuthUser.builder()
                .id(userId)
                .username(registerRequest.username())
                .password(encodedPassword)
                .email(registerRequest.email())
                .isEnabled(true)
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialNoExpired(true)
                .build();

        User user = User.builder()
                .id(userId)
                .userName(registerRequest.username())
                .name(name)
                .birthDate(birthDate)
                .books(new ArrayList<>())
                .build();

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(authUserRepository.save(any(AuthUser.class))).thenReturn(newUser);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(authUserRepository.existsByUsername(anyString())).thenReturn(false);
        when(authUserRepository.existsByEmail(anyString())).thenReturn(false);

        AuthRegisterResponse response = userDetailService.registerUser(registerRequest);

        assertNotNull(response, "The response should not be null");
        assertEquals(registerRequest.username(), response.username(), "Username should match");
        assertEquals(registerRequest.email(), response.email(), "Email should match");
        assertTrue(response.status(), "Status should be true");

        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(authUserRepository, times(1)).save(any(AuthUser.class));
        verify(userRepository, times(1)).save(any(User.class));
        verify(authUserRepository, times(1)).existsByUsername(registerRequest.username());
        verify(authUserRepository, times(1)).existsByEmail(registerRequest.email());
    }
}
