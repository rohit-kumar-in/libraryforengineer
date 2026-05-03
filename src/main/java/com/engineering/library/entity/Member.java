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
 * Represents a library member — a student, faculty member, or staff.
 */
@Entity
@Table(
    name = "members",
    indexes = {
        @Index(name = "idx_member_email",    columnList = "email",    unique = true),
        @Index(name = "idx_member_roll_no",  columnList = "rollNumber", unique = true)
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name must not be blank")
    @Size(max = 255)
    @Column(nullable = false)
    private String fullName;

    /** University roll number or employee ID. */
    @NotBlank(message = "Roll number must not be blank")
    @Column(nullable = false, unique = true, length = 20)
    private String rollNumber;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid address")
    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MemberRole role = MemberRole.STUDENT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MemberStatus status = MemberStatus.ACTIVE;

    /**
     * Current active borrows tracked here for O(1) limit check.
     * Kept in sync by BorrowService.
     */
    @Min(0) @Max(5)
    @Column(nullable = false)
    @Builder.Default
    private Integer activeBorrowCount = 0;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /* ───────────────────────── Enums ─────────────────────────── */

    public enum MemberRole   { STUDENT, FACULTY, STAFF }
    public enum MemberStatus { ACTIVE, SUSPENDED, GRADUATED }
}
