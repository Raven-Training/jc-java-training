package com.raven.training.presentation.dto.register;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"username", "email", "message", "status"})
public record AuthRegisterResponse(
        String username, String email ,String message, Boolean status
) {
}
