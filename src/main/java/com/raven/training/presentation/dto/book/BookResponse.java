package com.raven.training.presentation.dto.book;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing the response for a book.
 * This record is used to carry book-related data from the service layer to the client.
 * All its components are immutable and directly accessible.
 *
 * @param id The unique identifier of the book.
 * @param gender The genre of the book.
 * @param author The author's name.
 * @param image The URL or path to the book's cover image.
 * @param title The main title of the book.
 * @param subtitle The subtitle of the book.
 * @param publisher The name of the publisher.
 * @param year The publication year of the book.
 * @param pages The number of pages in the book.
 * @param isbn The International Standard Book Number (ISBN) for the book.
 */
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
