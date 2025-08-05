package com.raven.training.exception.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Exception thrown when a book is expected to be in a user's collection but is not found.
 * This exception is used to signal a specific business rule violation, for example,
 * when attempting to remove a book that a user does not own.
 * It automatically sets the HTTP status to 404 NOT FOUND.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookNotInCollectionException extends RuntimeException {

    /**
     * Constructs a new BookNotInCollectionException with a specific message
     * that includes the IDs of the user and the book.
     *
     * @param userId The unique identifier of the user.
     * @param bookId The unique identifier of the book that was not found in the collection.
     */
    public BookNotInCollectionException(UUID userId, UUID bookId) {
        super(String.format("The book with ID %s does not exist in the collection of the user with ID %s", bookId, userId));
    }
}
