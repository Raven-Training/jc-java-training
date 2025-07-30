package com.raven.training.persistence.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for AuthUserTest")
class AuthUserTest {

    private AuthUser authUser;
    private final UUID testId = UUID.randomUUID();
    private final LocalDateTime testTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        authUser = AuthUser.builder()
                .id(testId)
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .createdAt(testTime)
                .isEnabled(true)
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialNoExpired(true)
                .build();
    }

    @Test
    @DisplayName("Test AuthUser builder and getters")
    void testAuthUserBuilderAndGetters() {
        assertAll(
                () -> assertEquals(testId, authUser.getId(), "ID should match"),
                () -> assertEquals("testuser", authUser.getUsername(), "Username should match"),
                () -> assertEquals("password123", authUser.getPassword(), "Password should match"),
                () -> assertEquals("test@example.com", authUser.getEmail(), "Email should match"),
                () -> assertEquals(testTime, authUser.getCreatedAt(), "CreatedAt should match"),
                () -> assertTrue(authUser.isEnabled(), "isEnabled should be true"),
                () -> assertTrue(authUser.isAccountNoExpired(), "accountNoExpired should be true"),
                () -> assertTrue(authUser.isAccountNoLocked(), "accountNoLocked should be true"),
                () -> assertTrue(authUser.isCredentialNoExpired(), "credentialNoExpired should be true")
        );
    }

    @Test
    @DisplayName("Test prePersist with null ID")
    void testPrePersistWithNullId() {
        AuthUser newAuthUser = new AuthUser();
        newAuthUser.prePersist();
        
        assertNotNull(newAuthUser.getId(), "ID should be generated");
        assertNotNull(newAuthUser.getCreatedAt(), "CreatedAt should be set");
    }

    @Test
    @DisplayName("Test prePersist with existing ID")
    void testPrePersistWithExistingId() {
        UUID existingId = UUID.randomUUID();
        authUser.setId(existingId);
        authUser.setCreatedAt(null);
        
        authUser.prePersist();
        
        assertEquals(existingId, authUser.getId(), "ID should not change");
        assertNotNull(authUser.getCreatedAt(), "CreatedAt should be set");
    }

    @Test
    @DisplayName("Test equals and hashCode")
    void testEqualsAndHashCode() {
        AuthUser sameAuthUser = AuthUser.builder()
                .id(testId)
                .username("differentuser")
                .email("different@example.com")
                .build();

        AuthUser differentAuthUser = AuthUser.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .build();

        assertAll(
                () -> assertEquals(authUser, sameAuthUser, "Should be equal when IDs match"),
                () -> assertNotEquals(authUser, differentAuthUser, "Should not be equal when IDs differ"),
                () -> assertEquals(authUser.hashCode(), sameAuthUser.hashCode(), "Hash codes should match for equal objects")
        );
    }

    @Test
    @DisplayName("Test toString method")
    void testToString() {
        String toString = authUser.toString();

        assertAll(
                () -> assertTrue(toString.contains("id=" + testId), "Should contain ID"),
                () -> assertTrue(toString.contains("username=testuser"), "Should contain username"),
                () -> assertTrue(toString.contains("email=test@example.com"), "Should contain email")
        );
    }
}
