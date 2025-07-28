package com.raven.training.exception.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookAlreadyInCollectionException extends RuntimeException {

    public BookAlreadyInCollectionException(UUID userId, UUID bookId) {
        super(String.format("The book with ID %s already exist in the collection of the user with ID %s", bookId, userId));
    }
}
