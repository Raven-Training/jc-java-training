package com.raven.training.presentation.dto.login;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Data Transfer Object (DTO) representing the response for a successful user login.
 * This record is used to carry the authentication result from the service layer to the client.
 * It provides the username, a confirmation message, the JWT access token, and the status.
 *
 * @param username The username of the authenticated user.
 * @param message A confirmation message about the login status.
 * @param jwt The JWT (JSON Web Token) access token for subsequent authenticated requests.
 * @param status A boolean indicating the success of the login operation.
 */
@JsonPropertyOrder({"username", "message", "status", "jwt"})
public record AuthLoginResponse(
        String username, String message, String jwt, Boolean status
) {
}
