package com.raven.training.exception.handler;

import com.raven.training.exception.error.BookNotFoundException;
import com.raven.training.exception.error.UserNotFoundException;
import com.raven.training.persistence.model.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
    }

    // ==========================================================
    // Tests for methodArgumentNotValidException()
    // ==========================================================
    @Test
    @DisplayName("Should handle MethodArgumentNotValidException and return BAD_REQUEST")
    void methodArgumentNotValidException_ShouldReturnBadRequestErrorResponse() {
        String defaultMessage1 = "Field 'name' cannot be empty";
        String defaultMessage2 = "Field 'email' must be a valid email address";

        FieldError error1 = new FieldError("objectName", "name", defaultMessage1);
        FieldError error2 = new FieldError("objectName", "email", defaultMessage2);
        List<FieldError> fieldErrors = List.of(error1, error2);

        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.methodArgumentNotValidException(exception);

        assertNotNull(responseEntity, "ResponseEntity should not be null");
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode(), "Status should be BAD_REQUEST");

        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse, "ErrorResponse body should not be null");
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus(), "Status value should be 400");
        assertEquals("Validation failed", errorResponse.getMessage(), "Message should indicate validation failure");
        assertNotNull(errorResponse.getDetails(), "Details list should not be null");
        assertEquals(2, errorResponse.getDetails().size(), "Details list should contain 2 errors");
        assertTrue(errorResponse.getDetails().contains(defaultMessage1), "Details should contain first error message");
        assertTrue(errorResponse.getDetails().contains(defaultMessage2), "Details should contain second error message");
        assertNotNull(errorResponse.getTimestamp(), "Timestamp should be present");
        assertTrue(errorResponse.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)), "Timestamp should be current");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with no field errors")
    void methodArgumentNotValidException_NoFieldErrors_ShouldReturnEmptyDetails() {
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.methodArgumentNotValidException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertTrue(errorResponse.getDetails().isEmpty(), "Details list should be empty");
    }

    // ==========================================================
    // Tests for bookNotFoundException()
    // ==========================================================
    @Test
    @DisplayName("Should handle BookNotFoundException and return NOT_FOUND")
    void bookNotFoundException_ShouldReturnNotFoundErrorResponse() {
        String errorMessage = "Book with ID 123 not found";
        BookNotFoundException exception = new BookNotFoundException(errorMessage);

        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.bookNotFoundException(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals("Resource not found", errorResponse.getMessage());
        assertEquals(1, errorResponse.getDetails().size());
        assertEquals(errorMessage, errorResponse.getDetails().get(0));
        assertNotNull(errorResponse.getTimestamp());
    }

    // ==========================================================
    // Tests for userNotFoundException()
    // ==========================================================
    @Test
    @DisplayName("Should handle UserNotFoundException and return NOT_FOUND")
    void userNotFoundException_ShouldReturnNotFoundErrorResponse() {
        String errorMessage = "User with ID 456 not found";
        UserNotFoundException exception = new UserNotFoundException(errorMessage);

        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.userNotFoundException(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals("Resource not found", errorResponse.getMessage());
        assertEquals(1, errorResponse.getDetails().size());
        assertEquals(errorMessage, errorResponse.getDetails().get(0));
        assertNotNull(errorResponse.getTimestamp());
    }

    // ==========================================================
    // Tests for nullPointerException()
    // ==========================================================
    @Test
    @DisplayName("Should handle NullPointerException and return INTERNAL_SERVER_ERROR")
    void nullPointerException_ShouldReturnInternalServerErrorResponse() {
        String errorMessage = "Cannot invoke \"java.lang.String.length()\" because \"str\" is null";
        NullPointerException exception = new NullPointerException(errorMessage);

        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.nullPointerException(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
        assertEquals("Internal Server Error", errorResponse.getMessage());
        assertEquals(1, errorResponse.getDetails().size());
        assertEquals(errorMessage, errorResponse.getDetails().get(0));
        assertNotNull(errorResponse.getTimestamp());
    }

    // ==========================================================
    // Tests for generic exception()
    // ==========================================================
    @Test
    @DisplayName("Should handle generic Exception and return INTERNAL_SERVER_ERROR")
    void genericException_ShouldReturnInternalServerErrorResponse() {
        String errorMessage = "Something unexpected happened!";
        Exception exception = new Exception(errorMessage);

        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.exception(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
        assertEquals("An unexpected error occurred", errorResponse.getMessage());
        assertEquals(1, errorResponse.getDetails().size());
        assertEquals(errorMessage, errorResponse.getDetails().get(0));
        assertNotNull(errorResponse.getTimestamp());
    }
}
