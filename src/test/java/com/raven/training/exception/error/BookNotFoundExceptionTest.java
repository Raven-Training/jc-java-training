package com.raven.training.exception.error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Unit tests for BookNotFoundException")
class BookNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with no arguments")
    void constructor_NoArgs_ShouldCreateException() {
        BookNotFoundException exception = new BookNotFoundException();

        assertNotNull(exception);
        assertNull(exception.getMessage(), "Message should be null for no-arg constructor");
        assertNull(exception.getCause(), "Cause should be null for no-arg constructor");
    }

    @Test
    @DisplayName("Should create exception with a message")
    void constructor_WithMessage_ShouldCreateException() {
        String expectedMessage = "Book not found with the specified criteria.";

        BookNotFoundException exception = new BookNotFoundException(expectedMessage);

        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage(), "Message should match the provided message");
        assertNull(exception.getCause(), "Cause should be null for message-only constructor");
    }

    @Test
    @DisplayName("Should create exception with a cause")
    void constructor_WithCause_ShouldCreateException() {
        Throwable expectedCause = new IllegalStateException("Database connection failed.");

        BookNotFoundException exception = new BookNotFoundException(expectedCause);

        assertNotNull(exception);
        assertNotNull(exception.getMessage(), "Message should be derived from cause for cause-only constructor");
        assertSame(expectedCause, exception.getCause(), "Cause should match the provided cause");
    }

    @Test
    @DisplayName("Should create exception with message and cause")
    void constructor_WithMessageAndCause_ShouldCreateException() {
        String expectedMessage = "Failed to retrieve book due to an underlying issue.";
        Throwable expectedCause = new NullPointerException("A required object was null.");

        BookNotFoundException exception = new BookNotFoundException(expectedMessage, expectedCause);

        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage(), "Message should match the provided message");
        assertSame(expectedCause, exception.getCause(), "Cause should match the provided cause");
    }

    @Test
    @DisplayName("BookNotFoundException should be a RuntimeException")
    void shouldBeRuntimeException() {
        Executable executable = () -> {
            throw new BookNotFoundException("Test book exception");
        };

        assertThrows(RuntimeException.class, executable);
        assertThrows(BookNotFoundException.class, executable);
    }
}

