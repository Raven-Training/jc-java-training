package com.raven.training.presentation.dto.book;

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
