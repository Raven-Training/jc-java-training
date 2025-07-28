package com.raven.training.presentation.dto.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRegisterRequest(
        @NotBlank(message = "The username is obligatory") String username,
        @NotBlank(message = "The password is obligatory") String password,

        @Email(message = "Must be a valid email address")
        @NotBlank(message = "Email is required")
        String email
) {
}
