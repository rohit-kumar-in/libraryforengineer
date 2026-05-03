package com.engineering.library.controller;

import com.engineering.library.dto.BookRequestDto;
import com.engineering.library.dto.BookResponseDto;
import com.engineering.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for the book catalogue.
 *
 * <p>Base path: {@code /api/v1/books} (prefix set in application.yml)</p>
 *
 * <h3>Search Endpoint Design</h3>
 * Results from title/keyword searches are always ordered by {@code edition DESC}
 * so the most recent edition surfaces first — satisfying the Strict Edition
 * Search requirement.
 */
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Manage the library's book catalogue")
public class BookController {

    private final BookService bookService;

    /* ── CREATE ────────────────────────────────────────────────────────── */

    @PostMapping
    @Operation(
        summary  = "Register a new book",
        responses = {
            @ApiResponse(responseCode = "201", description = "Book created"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Duplicate ISBN")
        }
    )
    public ResponseEntity<BookResponseDto> createBook(
            @Valid @RequestBody BookRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(bookService.createBook(request));
    }

    /* ── READ ──────────────────────────────────────────────────────────── */

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by its internal ID")
    public ResponseEntity<BookResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping("/isbn/{isbn}")
    @Operation(summary = "Look up a book by ISBN-13")
    public ResponseEntity<BookResponseDto> getByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.getBookByIsbn(isbn));
    }

    /* ── SEARCH ────────────────────────────────────────────────────────── */

    /**
     * Title search — results ordered by edition DESC.
     *
     * <p>Example: {@code GET /books/search/title?q=algorithms&page=0&size=10}</p>
     */
    @GetMapping("/search/title")
    @Operation(
        summary = "Search books by title (latest edition first)",
        description = "Case-insensitive partial match on title, sorted by edition descending."
    )
    public ResponseEntity<Page<BookResponseDto>> searchByTitle(
            @Parameter(description = "Title keyword", example = "algorithms")
            @RequestParam String q,
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(bookService.searchByTitle(q, pageable));
    }

    /**
     * Full-text keyword search across title, author, and subject —
     * results also ordered by edition DESC.
     *
     * <p>Example: {@code GET /books/search?q=cormen&page=0&size=10}</p>
     */
    @GetMapping("/search")
    @Operation(
        summary = "Keyword search across title, author, and subject",
        description = "Case-insensitive search on multiple fields, sorted by edition descending."
    )
    public ResponseEntity<Page<BookResponseDto>> searchByKeyword(
            @Parameter(description = "Search keyword", example = "data structures")
            @RequestParam String q,
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(bookService.searchByKeyword(q, pageable));
    }

    @GetMapping
    @Operation(summary = "List all books (paginated)")
    public ResponseEntity<Page<BookResponseDto>> listAll(
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(bookService.listAllBooks(pageable));
    }

    @GetMapping("/available")
    @Operation(summary = "List only books with available copies")
    public ResponseEntity<Page<BookResponseDto>> listAvailable(
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(bookService.listAvailableBooks(pageable));
    }

    /* ── UPDATE ────────────────────────────────────────────────────────── */

    @PutMapping("/{id}")
    @Operation(summary = "Update a book's details")
    public ResponseEntity<BookResponseDto> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequestDto request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    /* ── DELETE ────────────────────────────────────────────────────────── */

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove a book from the catalogue")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
