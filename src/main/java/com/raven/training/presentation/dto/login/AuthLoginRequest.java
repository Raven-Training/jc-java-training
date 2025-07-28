package com.raven.training.presentation.dto.login;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequest(
        @NotBlank(message = "The username is obligatory") String username,
        @NotBlank(message = "The password is obligatory") String password
) {
}
