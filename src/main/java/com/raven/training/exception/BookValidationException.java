package com.raven.training.exception;

public class BookValidationException extends RuntimeException {

  public BookValidationException() {
    super();
  }

  public BookValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  public BookValidationException(Throwable cause) {
    super(cause);
  }

  public BookValidationException(String message) {
        super(message);
    }
}
