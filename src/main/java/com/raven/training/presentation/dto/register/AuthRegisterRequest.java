package com.raven.training.presentation.dto.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing a user registration request.
 * This record is used to carry the necessary data to create a new user,
 * including validation constraints for each field. All its components are
 * immutable and directly accessible.
 *
 * @param name The full name of the user. Cannot be blank.
 * @param birthDate The date of birth of the user. Cannot be null and must be a past or present date.
 * @param username The username for the new account. Cannot be blank.
 * @param password The password for the new account. Cannot be blank.
 * @param email The email address of the user. Must be a valid email and cannot be blank.
 */
public record AuthRegisterRequest(
        @NotBlank(message = "The username is obligatory") String name,

        @NotNull(message = "The username is obligatory")
        @PastOrPresent(message = "The date of birth must be a past or present date")
        LocalDate birthDate,

        @NotBlank(message = "The username is obligatory") String username,
        @NotBlank(message = "The password is obligatory") String password,

        @Email(message = "Must be a valid email address")
        @NotBlank(message = "Email is required")
        String email
) {
}
