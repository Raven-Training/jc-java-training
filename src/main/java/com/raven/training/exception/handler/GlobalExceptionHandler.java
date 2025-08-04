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

@RestControllerAdvice
public class GlobalExceptionHandler {
        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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
