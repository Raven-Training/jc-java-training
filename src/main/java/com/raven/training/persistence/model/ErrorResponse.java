package com.raven.training.persistence.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object (DTO) that represents a standardized API error response.
 * This class is used to provide consistent error information to the client,
 * including a list of detailed errors and the timestamp of when the error occurred.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private List<ApiError> errors;
    private LocalDateTime timestamp;
}
