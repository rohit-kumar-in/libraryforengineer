package com.engineering.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a physical or digital book in the library catalogue.
 *
 * <p>ISBN validation follows the ISBN-13 format (978/979 prefix).</p>
 */
@Entity
@Table(
    name = "books",
    indexes = {
        @Index(name = "idx_book_isbn",    columnList = "isbn",    unique = true),
        @Index(name = "idx_book_title",   columnList = "title"),
        @Index(name = "idx_book_edition", columnList = "edition")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Human-readable title. */
    @NotBlank(message = "Title must not be blank")
    @Size(max = 255, message = "Title must be at most 255 characters")
    @Column(nullable = false)
    private String title;

    /** Author full name or comma-separated list. */
    @NotBlank(message = "Author must not be blank")
    @Size(max = 255)
    @Column(nullable = false)
    private String author;

    /**
     * ISBN-13 format: 978-XXXXXXXXXX or 979-XXXXXXXXXX.
     * Stored without hyphens for uniform querying.
     */
    @NotBlank(message = "ISBN must not be blank")
    @Pattern(
        regexp = "^(978|979)\\d{10}$",
        message = "ISBN must be a valid 13-digit ISBN-13 (starts with 978 or 979)"
    )
    @Column(nullable = false, unique = true, length = 13)
    private String isbn;

    /**
     * Edition number — must be a positive integer (1st, 2nd, …).
     * Search results are sorted by this field descending so the latest
     * edition surfaces first.
     */
    @NotNull(message = "Edition must not be null")
    @Min(value = 1, message = "Edition must be a positive number (≥ 1)")
    @Max(value = 99, message = "Edition must be a realistic number (≤ 99)")
    @Column(nullable = false)
    private Integer edition;

    /** Dewey Decimal or custom catalogue number. */
    @Size(max = 50)
    private String callNumber;

    /** Publisher name. */
    @Size(max = 255)
    private String publisher;

    /** Year of publication. */
    @Min(value = 1800, message = "Publication year must be 1800 or later")
    @Max(value = 2100, message = "Publication year seems unrealistic")
    private Integer publicationYear;

    /** Total physical copies owned by the library. */
    @NotNull
    @Min(value = 0, message = "Total copies cannot be negative")
    @Column(nullable = false)
    @Builder.Default
    private Integer totalCopies = 1;

    /** How many copies are currently on the shelf. */
    @NotNull
    @Min(value = 0, message = "Available copies cannot be negative")
    @Column(nullable = false)
    @Builder.Default
    private Integer availableCopies = 1;

    /**
     * Convenience flag — kept in sync by {@link com.engineering.library.service.BorrowService}.
     * True when {@code availableCopies > 0}.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    /** Subject / genre tags (e.g. "Computer Science", "Thermodynamics"). */
    @Size(max = 255)
    private String subject;

    /** Active borrowing records associated with this book. */
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    /* ────────────────────────────── Audit ────────────────────────────── */

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /* ───────────────────────── Domain helpers ─────────────────────────── */

    /** Decrements availableCopies and refreshes the availability flag. */
    public void decrementAvailable() {
        if (this.availableCopies <= 0) {
            throw new IllegalStateException("No copies available to borrow for book id=" + id);
        }
        this.availableCopies--;
        this.isAvailable = this.availableCopies > 0;
    }

    /** Increments availableCopies and refreshes the availability flag. */
    public void incrementAvailable() {
        if (this.availableCopies >= this.totalCopies) {
            throw new IllegalStateException("All copies already returned for book id=" + id);
        }
        this.availableCopies++;
        this.isAvailable = true;
    }
}
