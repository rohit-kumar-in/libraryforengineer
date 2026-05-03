package com.engineering.library.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Centralised exception handler for the entire application.
 *
 * <p>All errors are returned as a consistent JSON envelope so clients
 * never receive raw Spring error pages.</p>
 *
 * <pre>
 * {
 *   "timestamp": "2024-06-01T10:30:00",
 *   "status":    404,
 *   "error":     "Not Found",
 *   "message":   "Book not found with id: 42",
 *   "path":      "/api/v1/books/42"
 * }
 * </pre>
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /* ─────────────── Domain Exceptions ─────────────── */

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ApiError> handleBookNotFound(
            BookNotFoundException ex, WebRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ApiError> handleMemberNotFound(
            MemberNotFoundException ex, WebRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(BorrowLimitExceededException.class)
    public ResponseEntity<ApiError> handleBorrowLimit(
            BorrowLimitExceededException ex, WebRequest request) {
        return buildError(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }

    @ExceptionHandler(BookNotAvailableException.class)
    public ResponseEntity<ApiError> handleBookNotAvailable(
            BookNotAvailableException ex, WebRequest request) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicateResource(
            DuplicateResourceException ex, WebRequest request) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    /* ─────────────── Validation Exceptions ─────────────── */

    /**
     * Handles @Valid failures on @RequestBody.
     * Returns a map of field → violation message for precise client feedback.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, String> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                fe -> fe.getDefaultMessage() == null ? "Invalid value" : fe.getDefaultMessage(),
                (msg1, msg2) -> msg1   // keep first message if duplicate field
            ));

        ApiValidationError error = ApiValidationError.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message("Validation failed. Check 'fieldErrors' for details.")
            .path(extractPath(request))
            .fieldErrors(fieldErrors)
            .build();

        return ResponseEntity.badRequest().body(error);
    }

    /** Handles @Validated path/query parameter violations. */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {

        String message = ex.getConstraintViolations()
            .stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining("; "));

        return buildError(HttpStatus.BAD_REQUEST, message, request);
    }

    /* ─────────────── Fallback ─────────────── */

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(
            IllegalStateException ex, WebRequest request) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex, WebRequest request) {
        logger.error("Unhandled exception", ex);
        return buildError(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred. Please contact support.",
            request
        );
    }

    /* ─────────────── Helpers ─────────────── */

    private ResponseEntity<ApiError> buildError(
            HttpStatus status, String message, WebRequest request) {

        ApiError error = ApiError.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(message)
            .path(extractPath(request))
            .build();

        return ResponseEntity.status(status).body(error);
    }

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    /* ─────────────── Error Envelope Records ─────────────── */

    @lombok.Getter @lombok.Builder
    public static class ApiError {
        private final LocalDateTime timestamp;
        private final int           status;
        private final String        error;
        private final String        message;
        private final String        path;
    }

    @lombok.Getter @lombok.Builder
    public static class ApiValidationError {
        private final LocalDateTime         timestamp;
        private final int                   status;
        private final String                error;
        private final String                message;
        private final String                path;
        private final Map<String, String>   fieldErrors;
    }
}
