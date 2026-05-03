package com.engineering.library.service;

import com.engineering.library.dto.BorrowRecordResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BorrowService {

    /** Borrow a book — runs all pre-checks and creates the BorrowRecord. */
    BorrowRecordResponseDto borrowBook(Long memberId, Long bookId);

    /** Return a book — marks the record RETURNED and restores availability. */
    BorrowRecordResponseDto returnBook(Long memberId, Long bookId);

    /** Active borrows for a given member. */
    Page<BorrowRecordResponseDto> getMemberBorrows(Long memberId, Pageable pageable);

    /** All borrow history for a given book. */
    Page<BorrowRecordResponseDto> getBookBorrows(Long bookId, Pageable pageable);
}
