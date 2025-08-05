package com.raven.training.exception.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Exception thrown when an attempt is made to add a book that already exists
 * in a user's collection.
 * This exception signals a business rule violation and provides a clear error
 * response, automatically setting the HTTP status to 409 CONFLICT.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class BookAlreadyInCollectionException extends RuntimeException {

    /**
     * Constructs a new BookAlreadyInCollectionException with a specific message
     * that includes the IDs of the user and the book.
     *
     * @param userId The unique identifier of the user.
     * @param bookId The unique identifier of the book that already exists in the collection.
     */
    public BookAlreadyInCollectionException(UUID userId, UUID bookId) {
        super(String.format("The book with ID %s already exist in the collection of the user with ID %s", bookId, userId));
    }
}
