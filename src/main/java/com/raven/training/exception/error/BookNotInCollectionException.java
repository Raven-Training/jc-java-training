package com.raven.training.exception.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookNotInCollectionException extends RuntimeException {

    public BookNotInCollectionException(UUID userId, UUID bookId) {
        super(String.format("The book with ID %s does not exist in the collection of the user with ID %s", bookId, userId));
    }
}
