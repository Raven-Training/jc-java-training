package com.raven.training.presentation.dto.register;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Data Transfer Object (DTO) representing the response for a successful user registration.
 * This record is used to carry the registration result from the service layer to the client.
 * It provides the username, email, a confirmation message, and the status.
 *
 * @param username The username of the newly registered user.
 * @param email The email address of the newly registered user.
 * @param message A confirmation message about the registration status.
 * @param status A boolean indicating the success of the registration operation.
 */
@JsonPropertyOrder({"username", "email", "message", "status"})
public record AuthRegisterResponse(
        String username, String email ,String message, Boolean status
) {
}
