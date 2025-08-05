package com.raven.training.exception.error;

/**
 * Exception thrown when an attempt is made to create a new user
 * with a username that already exists in the system.
 * This exception signals a business rule violation and helps in
 * providing a clear, descriptive error response to the client.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
public class UsernameAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new UsernameAlreadyExistsException with the specified detail message.
     *
     * @param message The detail message.
     */
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
