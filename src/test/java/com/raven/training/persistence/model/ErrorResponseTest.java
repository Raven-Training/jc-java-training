package com.raven.training.persistence.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for ErrorResponse DTO")
class ErrorResponseTest {

    @Test
    @DisplayName("Should create ErrorResponse using builder and verify getters")
    void builder_and_getters_ShouldWorkCorrectly() {
        Integer expectedStatus = 400;
        String expectedMessage = "Test Message";
        List<String> expectedDetails = Arrays.asList("Detail 1", "Detail 2");
        LocalDateTime expectedTimestamp = LocalDateTime.now();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(expectedStatus)
                .message(expectedMessage)
                .details(expectedDetails)
                .timestamp(expectedTimestamp)
                .build();

        assertEquals(expectedStatus, errorResponse.getStatus(), "Status should match");
        assertEquals(expectedMessage, errorResponse.getMessage(), "Message should match");
        assertEquals(expectedDetails, errorResponse.getDetails(), "Details should match");
        assertEquals(expectedTimestamp, errorResponse.getTimestamp(), "Timestamp should match");
    }

    @Test
    @DisplayName("Should allow setting and getting all fields using setters and getters")
    void setters_and_getters_ShouldWorkCorrectly() {
        ErrorResponse errorResponse = new ErrorResponse();

        Integer newStatus = 500;
        String newMessage = "New Test Message";
        List<String> newDetails = Arrays.asList("New Detail 1", "New Detail 2");
        LocalDateTime newTimestamp = LocalDateTime.now().plusHours(1);

        errorResponse.setStatus(newStatus);
        errorResponse.setMessage(newMessage);
        errorResponse.setDetails(newDetails);
        errorResponse.setTimestamp(newTimestamp);

        assertEquals(newStatus, errorResponse.getStatus(), "Status should be updated via setter");
        assertEquals(newMessage, errorResponse.getMessage(), "Message should be updated via setter");
        assertEquals(newDetails, errorResponse.getDetails(), "Details should be updated via setter");
        assertEquals(newTimestamp, errorResponse.getTimestamp(), "Timestamp should be updated via setter");
    }

    @Test
    @DisplayName("Should handle null values for setters gracefully")
    void setters_ShouldHandleNullValues() {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setStatus(null);
        errorResponse.setMessage(null);
        errorResponse.setDetails(null);
        errorResponse.setTimestamp(null);

        assertNull(errorResponse.getStatus());
        assertNull(errorResponse.getMessage());
        assertNull(errorResponse.getDetails());
        assertNull(errorResponse.getTimestamp());
    }
}
