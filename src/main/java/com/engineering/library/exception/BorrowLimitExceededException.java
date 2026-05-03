package com.engineering.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a member has reached the 5-book borrow limit.
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class BorrowLimitExceededException extends RuntimeException {

    public BorrowLimitExceededException(Long memberId, int limit) {
        super(String.format(
            "Member %d has reached the maximum borrow limit of %d books.", memberId, limit
        ));
    }
}
