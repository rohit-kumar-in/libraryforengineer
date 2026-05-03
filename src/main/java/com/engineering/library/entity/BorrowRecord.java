package com.engineering.library.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Records a single borrow or return transaction.
 *
 * <p>Status lifecycle: {@code ACTIVE} → {@code RETURNED} or {@code OVERDUE}.</p>
 */
@Entity
@Table(
    name = "borrow_records",
    indexes = {
        @Index(name = "idx_br_member", columnList = "member_id"),
        @Index(name = "idx_br_book",   columnList = "book_id"),
        @Index(name = "idx_br_status", columnList = "status")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /** Date on which the book was borrowed. */
    @Column(nullable = false)
    private LocalDate borrowDate;

    /** Expected return date (borrowDate + loan duration from config). */
    @Column(nullable = false)
    private LocalDate dueDate;

    /** Actual return date; null while book is still out. */
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BorrowStatus status = BorrowStatus.ACTIVE;

    /** Optional librarian notes (e.g. condition on return). */
    private String notes;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /* ───────────────────────── Enums ─────────────────────────── */

    public enum BorrowStatus { ACTIVE, RETURNED, OVERDUE }
}
