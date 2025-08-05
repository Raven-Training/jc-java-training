package com.raven.training.exception.error;

/**
 * Exception thrown when a book is not found in the system.
 * This is a custom runtime exception that indicates a book lookup operation
 * has failed because the book does not exist.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
public class BookNotFoundException extends RuntimeException {

  /**
   * Constructs a new BookNotFoundException with a default message.
   */
  public BookNotFoundException() {
    super();
  }

  /**
   * Constructs a new BookNotFoundException with the specified detail message and cause.
   *
   * @param message The detail message.
   * @param cause The cause of the exception.
   */
  public BookNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new BookNotFoundException with the specified cause.
   *
   * @param cause The cause of the exception.
   */
  public BookNotFoundException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new BookNotFoundException with the specified detail message.
   *
   * @param message The detail message.
   */
  public BookNotFoundException(String message) {
        super(message);
    }
}
