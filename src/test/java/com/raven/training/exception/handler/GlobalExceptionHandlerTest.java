package com.raven.training.exception.handler;

import com.raven.training.persistence.model.ApiError;
import com.raven.training.persistence.model.ErrorResponse;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private BindingResult bindingResult;

    private static class BookNotFoundException extends RuntimeException {
        public BookNotFoundException(String message) {
            super(message);
        }
    }

    private static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

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
        assertNotNull(errorResponse.getErrors(), "Errors list should not be null");
        assertEquals(2, errorResponse.getErrors().size(), "Errors list should contain 2 errors");

        ApiError apiError1 = errorResponse.getErrors().get(0);
        assertEquals("0100", apiError1.getCode(), "Error code should be '0100'");
        assertEquals(defaultMessage1, apiError1.getMessage(), "Error message should match the field error");

        ApiError apiError2 = errorResponse.getErrors().get(1);
        assertEquals("0100", apiError2.getCode(), "Error code should be '0100'");
        assertEquals(defaultMessage2, apiError2.getMessage(), "Error message should match the field error");

        assertNotNull(errorResponse.getTimestamp(), "Timestamp should be present");
        assertTrue(errorResponse.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)), "Timestamp should be current");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with no field errors")
    void methodArgumentNotValidException_NoFieldErrors_ShouldReturnEmptyErrorsList() {
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.methodArgumentNotValidException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertTrue(errorResponse.getErrors().isEmpty(), "Errors list should be empty");
    }

    @Test
    @DisplayName("Should handle BookNotFoundException and return NOT_FOUND")
    void resourceNotFoundException_BookNotFound_ShouldReturnNotFoundErrorResponse() {
        String errorMessage = "Book with ID 123 not found";
        BookNotFoundException exception = new BookNotFoundException(errorMessage);

        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.resourceNotFoundException(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(1, errorResponse.getErrors().size());

        ApiError apiError = errorResponse.getErrors().get(0);
        assertEquals("0200", apiError.getCode());
        assertEquals(errorMessage, apiError.getMessage());

        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    @DisplayName("Should handle UserNotFoundException and return NOT_FOUND")
    void resourceNotFoundException_UserNotFound_ShouldReturnNotFoundErrorResponse() {
        String errorMessage = "User with ID 456 not found";
        UserNotFoundException exception = new UserNotFoundException(errorMessage);

        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.resourceNotFoundException(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(1, errorResponse.getErrors().size());

        ApiError apiError = errorResponse.getErrors().get(0);
        assertEquals("0200", apiError.getCode());
        assertEquals(errorMessage, apiError.getMessage());

        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    @DisplayName("Should handle NullPointerException and return INTERNAL_SERVER_ERROR")
    void nullPointerException_ShouldReturnInternalServerErrorResponse() {
        NullPointerException exception = new NullPointerException();

        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.nullPointerException(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(1, errorResponse.getErrors().size());

        ApiError apiError = errorResponse.getErrors().get(0);
        assertEquals("9000", apiError.getCode());
        assertEquals("An internal error occurred: Null reference.", apiError.getMessage());

        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    @DisplayName("Should handle generic Exception and return INTERNAL_SERVER_ERROR")
    void genericException_ShouldReturnInternalServerErrorResponse() {
        Exception exception = new Exception();

        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.exception(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(1, errorResponse.getErrors().size());

        ApiError apiError = errorResponse.getErrors().get(0);
        assertEquals("9999", apiError.getCode());
        assertEquals("An unexpected error occurred.", apiError.getMessage());

        assertNotNull(errorResponse.getTimestamp());
    }
}
