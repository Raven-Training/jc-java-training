package com.raven.training.presentation.dto.book;

import java.util.Set;
import java.util.UUID;

public record BookRequest(
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
