package com.raven.training.persistence.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Unit tests for ErrorResponse DTO")
class ErrorResponseTest {

    @Test
    @DisplayName("Should create ErrorResponse using builder and verify getters")
    void builderAndGetters_ShouldWorkCorrectly() {
        List<ApiError> expectedErrors = Arrays.asList(
                new ApiError("INVALID_FIELD", "The 'name' field is required"),
                new ApiError("INVALID_FORMAT", "The email format is incorrect")
        );

        LocalDateTime expectedTimestamp = LocalDateTime.now();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(expectedErrors)
                .timestamp(expectedTimestamp)
                .build();

        assertNotNull(errorResponse.getErrors(), "Errors list should not be null");
        assertEquals(expectedErrors.size(), errorResponse.getErrors().size(), "Errors list size should match");
        assertEquals(expectedErrors, errorResponse.getErrors(), "Errors list should match");
        assertEquals(expectedTimestamp, errorResponse.getTimestamp(), "Timestamp should match");
    }

    @Test
    @DisplayName("Should allow setting and getting all fields using setters and getters")
    void settersAndGetters_ShouldWorkCorrectly() {
        ErrorResponse errorResponse = new ErrorResponse();

        List<ApiError> newErrors = Arrays.asList(
                new ApiError("SERVER_ERROR", "Internal server error")
        );
        LocalDateTime newTimestamp = LocalDateTime.now().plusHours(1);

        errorResponse.setErrors(newErrors);
        errorResponse.setTimestamp(newTimestamp);

        assertEquals(newErrors, errorResponse.getErrors(), "Errors list should be updated via setter");
        assertEquals(newTimestamp, errorResponse.getTimestamp(), "Timestamp should be updated via setter");
    }

    @Test
    @DisplayName("Should handle null values for setters gracefully")
    void setters_ShouldHandleNullValues() {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setErrors(null);
        errorResponse.setTimestamp(null);

        assertNull(errorResponse.getErrors(), "Errors list should be null");
        assertNull(errorResponse.getTimestamp(), "Timestamp should be null");
    }
}
