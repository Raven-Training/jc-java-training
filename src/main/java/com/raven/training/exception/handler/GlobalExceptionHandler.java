package com.raven.training.exception.handler;

import com.raven.training.exception.error.BookNotFoundException;
import com.raven.training.exception.error.UserNotFoundException;
import com.raven.training.persistence.model.ApiError;
import com.raven.training.persistence.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for the REST API.
 * This class captures and handles various exceptions thrown by controllers
 * and other components, providing a standardized and consistent error response format.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles validation errors when a method argument fails validation.
     * This exception is typically thrown when a DTO with validation annotations
     * (e.g., @NotBlank, @Size) receives invalid data from a client request.
     *
     * @param exception The {@link MethodArgumentNotValidException} that occurred.
     * @return A {@link ResponseEntity} with a {@link ErrorResponse} containing
     * a list of validation errors and an HTTP status of 400 (Bad Request).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException exception){
        List<ApiError> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiError("0100", error.getDefaultMessage()))
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions for resources that are not found.
     * This method catches {@link BookNotFoundException} and {@link UserNotFoundException},
     * returning a standardized error message.
     *
     * @param exception The {@link RuntimeException} that occurred (e.g., a Not Found exception).
     * @return A {@link ResponseEntity} with a {@link ErrorResponse} containing
     * a single error and an HTTP status of 404 (Not Found).
     */
    @ExceptionHandler({BookNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<ErrorResponse> resourceNotFoundException(RuntimeException exception){
        String errorMessage = exception.getMessage() != null ? exception.getMessage() : "Resource not found";

        ApiError error = new ApiError("0200", errorMessage);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(Collections.singletonList(error))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles unexpected {@link NullPointerException}s.
     * This is a generic fallback handler for null pointer issues, logging the error
     * for debugging purposes and returning a generic internal server error to the client.
     *
     * @param exception The {@link NullPointerException} that occurred.
     * @return A {@link ResponseEntity} with a {@link ErrorResponse} and an
     * HTTP status of 500 (Internal Server Error).
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> nullPointerException(NullPointerException exception){
        logger.error("A has been capture NullPointerException", exception);
        ApiError error = new ApiError("9000", "An internal error occurred: Null reference.");
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(Collections.singletonList(error))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles all other uncaught exceptions.
     * This is the final fallback handler for any exception not handled by
     * a more specific handler, ensuring a consistent error response.
     *
     * @param exception The generic {@link Exception} that occurred.
     * @return A {@link ResponseEntity} with a {@link ErrorResponse} and an
     * HTTP status of 500 (Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception(Exception exception){
        logger.error("A has been capture Exception", exception);
        ApiError error = new ApiError("9999", "An unexpected error occurred.");
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(Collections.singletonList(error))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
