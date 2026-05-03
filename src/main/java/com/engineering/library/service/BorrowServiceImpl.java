package com.engineering.library.service;

import com.engineering.library.dto.BorrowRecordResponseDto;
import com.engineering.library.entity.*;
import com.engineering.library.entity.BorrowRecord.BorrowStatus;
import com.engineering.library.exception.*;
import com.engineering.library.mapper.BorrowRecordMapper;
import com.engineering.library.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Core business logic for borrowing and returning books.
 *
 * <h3>Borrow Workflow</h3>
 * <ol>
 *   <li>Validate member exists and is ACTIVE.</li>
 *   <li>Validate book exists.</li>
 *   <li>Check {@code isAvailable} / {@code availableCopies > 0}.</li>
 *   <li>Enforce the 5-book-per-member limit.</li>
 *   <li>Prevent duplicate active borrows (same member + same book).</li>
 *   <li>Decrement {@code availableCopies} on the book entity.</li>
 *   <li>Increment {@code activeBorrowCount} on the member entity.</li>
 *   <li>Persist a {@link BorrowRecord}.</li>
 * </ol>
 *
 * <p>The entire method is wrapped in {@code @Transactional} so any failure
 * rolls back all partial state changes — preventing phantom borrows or
 * inventory inconsistency.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordRepository borrowRepo;
    private final BookRepository         bookRepo;
    private final MemberRepository       memberRepo;
    private final BorrowRecordMapper     borrowMapper;

    /** Configurable via application.yml — default 5. */
    @Value("${library.borrow.max-books-per-member:5}")
    private int maxBooksPerMember;

    /** Loan duration in days — default 14. */
    @Value("${library.borrow.loan-duration-days:14}")
    private int loanDurationDays;

    /* ══════════════════════════════════════════════════════════════════
       BORROW
       ══════════════════════════════════════════════════════════════════ */

    @Override
    @Transactional          // ← single transaction: book + member + record
    public BorrowRecordResponseDto borrowBook(Long memberId, Long bookId) {

        // 1. Load & validate member
        Member member = memberRepo.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(memberId));

        if (member.getStatus() != Member.MemberStatus.ACTIVE) {
            throw new IllegalStateException(
                "Member " + memberId + " is not active (status=" + member.getStatus() + ")"
            );
        }

        // 2. Load & validate book
        Book book = bookRepo.findById(bookId)
            .orElseThrow(() -> new BookNotFoundException(bookId));

        // 3. Availability check
        if (!book.getIsAvailable() || book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException(bookId);
        }

        // 4. Borrow-limit check  (double-checked at DB level by countByMemberIdAndStatus)
        long activeCount = borrowRepo.countByMemberIdAndStatus(memberId, BorrowStatus.ACTIVE);
        if (activeCount >= maxBooksPerMember) {
            throw new BorrowLimitExceededException(memberId, maxBooksPerMember);
        }

        // 5. Duplicate-borrow guard
        if (borrowRepo.existsByMemberIdAndBookIdAndStatus(memberId, bookId, BorrowStatus.ACTIVE)) {
            throw new IllegalStateException(
                "Member " + memberId + " already has an active borrow for book " + bookId
            );
        }

        // 6. Update inventory — entity method keeps availableCopies + isAvailable in sync
        book.decrementAvailable();

        // 7. Update member's borrow counter
        member.setActiveBorrowCount(member.getActiveBorrowCount() + 1);

        // 8. Create the record
        LocalDate today   = LocalDate.now();
        BorrowRecord record = BorrowRecord.builder()
            .member(member)
            .book(book)
            .borrowDate(today)
            .dueDate(today.plusDays(loanDurationDays))
            .status(BorrowStatus.ACTIVE)
            .build();

        BorrowRecord saved = borrowRepo.save(record);
        log.info("BORROW — member={} book={} dueDate={}", memberId, bookId, saved.getDueDate());

        return borrowMapper.toResponseDto(saved);
    }

    /* ══════════════════════════════════════════════════════════════════
       RETURN
       ══════════════════════════════════════════════════════════════════ */

    @Override
    @Transactional
    public BorrowRecordResponseDto returnBook(Long memberId, Long bookId) {

        // Find the active borrow record
        BorrowRecord record = borrowRepo
            .findByMemberIdAndBookIdAndStatus(memberId, bookId, BorrowStatus.ACTIVE)
            .orElseThrow(() -> new IllegalStateException(
                "No active borrow found for member=" + memberId + " book=" + bookId
            ));

        Member member = record.getMember();
        Book   book   = record.getBook();

        // Mark returned
        record.setReturnDate(LocalDate.now());
        record.setStatus(BorrowStatus.RETURNED);

        // Restore inventory
        book.incrementAvailable();

        // Decrement member counter (never below 0)
        member.setActiveBorrowCount(Math.max(0, member.getActiveBorrowCount() - 1));

        BorrowRecord saved = borrowRepo.save(record);
        log.info("RETURN — member={} book={} returnDate={}", memberId, bookId, saved.getReturnDate());

        return borrowMapper.toResponseDto(saved);
    }

    /* ══════════════════════════════════════════════════════════════════
       QUERIES
       ══════════════════════════════════════════════════════════════════ */

    @Override
    @Transactional(readOnly = true)
    public Page<BorrowRecordResponseDto> getMemberBorrows(Long memberId, Pageable pageable) {
        // Confirm member exists first
        if (!memberRepo.existsById(memberId)) {
            throw new MemberNotFoundException(memberId);
        }
        return borrowRepo.findByMemberId(memberId, pageable)
            .map(borrowMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BorrowRecordResponseDto> getBookBorrows(Long bookId, Pageable pageable) {
        if (!bookRepo.existsById(bookId)) {
            throw new BookNotFoundException(bookId);
        }
        return borrowRepo.findByBookId(bookId, pageable)
            .map(borrowMapper::toResponseDto);
    }
}
