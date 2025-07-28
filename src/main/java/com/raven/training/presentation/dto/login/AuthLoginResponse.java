package com.raven.training.presentation.dto.login;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"username", "message", "status", "jwt"})
public record AuthLoginResponse(
        String username, String message, String jwt, Boolean status
) {
}
