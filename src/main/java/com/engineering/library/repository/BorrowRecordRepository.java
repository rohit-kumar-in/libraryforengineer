package com.engineering.library.repository;

import com.engineering.library.entity.BorrowRecord;
import com.engineering.library.entity.BorrowRecord.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    /** Used to enforce the 5-book-per-member limit check at the DB level. */
    long countByMemberIdAndStatus(Long memberId, BorrowStatus status);

    /** Detects an already-active borrow of the same book by the same member. */
    boolean existsByMemberIdAndBookIdAndStatus(Long memberId, Long bookId, BorrowStatus status);

    Page<BorrowRecord> findByMemberId(Long memberId, Pageable pageable);
    Page<BorrowRecord> findByBookId(Long bookId, Pageable pageable);

    /** Returns all overdue active records — used by a scheduled job. */
    @Query("""
        SELECT r FROM BorrowRecord r
        WHERE r.status = 'ACTIVE'
          AND r.dueDate < :today
        """)
    List<BorrowRecord> findOverdueRecords(@Param("today") LocalDate today);

    /** Active borrow of a specific book by a specific member. */
    Optional<BorrowRecord> findByMemberIdAndBookIdAndStatus(
        Long memberId, Long bookId, BorrowStatus status
    );
}
