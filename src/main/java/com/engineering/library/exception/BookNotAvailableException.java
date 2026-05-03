package com.engineering.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a book has no available copies left.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class BookNotAvailableException extends RuntimeException {

    public BookNotAvailableException(Long bookId) {
        super("No available copies for book id: " + bookId);
    }
}
