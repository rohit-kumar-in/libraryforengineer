package com.engineering.library.controller;

import com.engineering.library.dto.BorrowRecordResponseDto;
import com.engineering.library.service.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for borrowing and returning books.
 *
 * <p>All operations that mutate state (borrow / return) are idempotent-safe
 * because the service layer checks for existing active records.</p>
 */
@RestController
@RequestMapping("/borrows")
@RequiredArgsConstructor
@Tag(name = "Borrowing", description = "Handle book borrowing and return transactions")
public class BorrowController {

    private final BorrowService borrowService;

    /**
     * Borrow a book.
     *
     * <pre>POST /borrows/borrow?memberId=1&amp;bookId=42</pre>
     */
    @PostMapping("/borrow")
    @Operation(
        summary = "Borrow a book",
        description = """
            Executes the full borrow workflow:
            1. Checks book availability
            2. Enforces the 5-book-per-member limit
            3. Prevents duplicate active borrows
            4. Creates a BorrowRecord (transactional)
            """,
        responses = {
            @ApiResponse(responseCode = "201", description = "Book borrowed successfully"),
            @ApiResponse(responseCode = "404", description = "Member or book not found"),
            @ApiResponse(responseCode = "409", description = "Book not available / already borrowed"),
            @ApiResponse(responseCode = "422", description = "Borrow limit exceeded")
        }
    )
    public ResponseEntity<BorrowRecordResponseDto> borrowBook(
            @Parameter(description = "ID of the member borrowing the book", required = true)
            @RequestParam Long memberId,
            @Parameter(description = "ID of the book to borrow", required = true)
            @RequestParam Long bookId) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(borrowService.borrowBook(memberId, bookId));
    }

    /**
     * Return a borrowed book.
     *
     * <pre>POST /borrows/return?memberId=1&amp;bookId=42</pre>
     */
    @PostMapping("/return")
    @Operation(
        summary = "Return a borrowed book",
        description = "Marks the active BorrowRecord as RETURNED and restores book availability.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Book returned successfully"),
            @ApiResponse(responseCode = "404", description = "No active borrow found")
        }
    )
    public ResponseEntity<BorrowRecordResponseDto> returnBook(
            @RequestParam Long memberId,
            @RequestParam Long bookId) {
        return ResponseEntity.ok(borrowService.returnBook(memberId, bookId));
    }

    /** All borrow records (active and historical) for a given member. */
    @GetMapping("/member/{memberId}")
    @Operation(summary = "Get all borrow records for a member")
    public ResponseEntity<Page<BorrowRecordResponseDto>> getMemberBorrows(
            @PathVariable Long memberId,
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(borrowService.getMemberBorrows(memberId, pageable));
    }

    /** All borrow records for a given book. */
    @GetMapping("/book/{bookId}")
    @Operation(summary = "Get all borrow records for a book")
    public ResponseEntity<Page<BorrowRecordResponseDto>> getBookBorrows(
            @PathVariable Long bookId,
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(borrowService.getBookBorrows(bookId, pageable));
    }
}
