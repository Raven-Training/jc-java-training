package com.raven.training.persistence.model;

import lombok.*;

/**
 * Data Transfer Object (DTO) that represents a standardized API error response.
 * This class is used to provide consistent error information to the client,
 * including an error code and a descriptive message.
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
public class ApiError {

    private String code;
    private String message;
}
