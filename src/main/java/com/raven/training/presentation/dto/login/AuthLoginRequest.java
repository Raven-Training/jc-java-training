package com.raven.training.presentation.dto.login;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object (DTO) representing a user login request.
 * This record is used to carry the user's login credentials from the client
 * to the service layer for authentication. All its components are immutable
 * and directly accessible.
 *
 * @param username The username of the user. Cannot be blank.
 * @param password The password of the user. Cannot be blank.
 */
public record AuthLoginRequest(
        @NotBlank(message = "The username is obligatory") String username,
        @NotBlank(message = "The password is obligatory") String password
) {
}
