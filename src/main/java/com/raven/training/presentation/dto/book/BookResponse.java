package com.raven.training.presentation.dto.book;

import java.util.UUID;

public record BookResponse(
        UUID id,
        String gender,
        String author,
        String image,
        String title,
        String subtitle,
        String publisher,
        String year,
        Integer pages,
        String isbn
) {
}
