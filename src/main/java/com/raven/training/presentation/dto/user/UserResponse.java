package com.raven.training.presentation.dto.user;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String userName,
        String name,
        LocalDate birthDate,
        List<UUID> bookIds
) {
}
