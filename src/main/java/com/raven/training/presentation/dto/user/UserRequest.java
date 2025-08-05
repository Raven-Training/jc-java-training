package com.raven.training.presentation.dto.user;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a user update request.
 * This record is used to carry updated user data from the client to the service layer.
 * All its components are immutable and directly accessible.
 *
 * @param userName The username for the user's account.
 * @param name The full name of the user.
 * @param birthDate The date of birth of the user.
 * @param bookIds A list of UUIDs representing the books to be associated with the user.
 */
public record UserRequest(
        String userName,
        String name,
        LocalDate birthDate,
        List<UUID> bookIds
) {
}
