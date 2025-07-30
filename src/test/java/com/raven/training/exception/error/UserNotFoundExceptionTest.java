package com.raven.training.exception.error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Unit tests for UserNotFoundException")
class UserNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with no arguments")
    void constructor_NoArgs_ShouldCreateException() {
        UserNotFoundException exception = new UserNotFoundException();

        assertNotNull(exception);
        assertNull(exception.getMessage(), "Message should be null for no-arg constructor");
        assertNull(exception.getCause(), "Cause should be null for no-arg constructor");
    }

    @Test
    @DisplayName("Should create exception with a message")
    void constructor_WithMessage_ShouldCreateException() {
        String expectedMessage = "User not found with provided ID.";

        UserNotFoundException exception = new UserNotFoundException(expectedMessage);

        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage(), "Message should match the provided message");
        assertNull(exception.getCause(), "Cause should be null for message-only constructor");
    }

    @Test
    @DisplayName("Should create exception with a cause")
    void constructor_WithCause_ShouldCreateException() {
        Throwable expectedCause = new RuntimeException("Original cause of error.");

        UserNotFoundException exception = new UserNotFoundException(expectedCause);

        assertNotNull(exception);
        assertNotNull(exception.getMessage(), "Message should be derived from cause for cause-only constructor");
        assertSame(expectedCause, exception.getCause(), "Cause should match the provided cause");
    }

    @Test
    @DisplayName("Should create exception with message and cause")
    void constructor_WithMessageAndCause_ShouldCreateException() {
        String expectedMessage = "User not found due to a lower-level issue.";
        Throwable expectedCause = new IllegalStateException("Problem in data access layer.");

        UserNotFoundException exception = new UserNotFoundException(expectedMessage, expectedCause);

        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage(), "Message should match the provided message");
        assertSame(expectedCause, exception.getCause(), "Cause should match the provided cause");
    }

    @Test
    @DisplayName("UserNotFoundException should be a RuntimeException")
    void shouldBeRuntimeException() {
        Executable executable = () -> {
            throw new UserNotFoundException("Test exception");
        };

        assertThrows(RuntimeException.class, executable);
        assertThrows(UserNotFoundException.class, executable);
    }
}
