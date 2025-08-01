package com.raven.training.presentation.dto.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

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
