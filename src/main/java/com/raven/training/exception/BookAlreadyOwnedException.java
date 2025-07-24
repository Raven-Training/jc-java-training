package com.raven.training.exception;

public class BookAlreadyOwnedException extends RuntimeException {

  public BookAlreadyOwnedException() {
    super();
  }

  public BookAlreadyOwnedException(String message, Throwable cause) {
    super(message, cause);
  }

  public BookAlreadyOwnedException(Throwable cause) {
    super(cause);
  }

  public BookAlreadyOwnedException(String message) {
        super(message);
    }
}
