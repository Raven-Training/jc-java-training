package com.raven.training.presentation.dto.user;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing the response for a user.
 * This record is used to carry user-related data from the service layer to the client.
 * All its components are immutable and directly accessible.
 *
 * @param id The unique identifier of the user.
 * @param userName The username for the user's account.
 * @param name The full name of the user.
 * @param birthDate The date of birth of the user.
 * @param bookIds A list of UUIDs representing the books associated with the user.
 */
public record UserResponse(
        UUID id,
        String userName,
        String name,
        LocalDate birthDate,
        List<UUID> bookIds
) {
}
