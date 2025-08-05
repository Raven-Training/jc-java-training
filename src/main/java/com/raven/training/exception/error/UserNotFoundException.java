package com.raven.training.exception.error;

/**
 * Exception thrown when a user is not found in the system.
 * This is a custom runtime exception that indicates a user lookup operation
 * has failed because the user does not exist.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new UserNotFoundException with a default message.
     */
    public UserNotFoundException() {
        super();
    }

    /**
     * Constructs a new UserNotFoundException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause The cause of the exception.
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new UserNotFoundException with the specified cause.
     *
     * @param cause The cause of the exception.
     */
    public UserNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     *
     * @param message The detail message.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
