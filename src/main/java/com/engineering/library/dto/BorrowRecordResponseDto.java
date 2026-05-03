package com.engineering.library.dto;

import com.engineering.library.entity.BorrowRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for a borrow / return transaction record.
 */
@Schema(description = "Details of a borrowing transaction")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRecordResponseDto {

    private Long id;

    @Schema(description = "Member who borrowed the book")
    private Long memberId;
    private String memberName;
    private String memberRollNumber;

    @Schema(description = "Borrowed book details")
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;

    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    private BorrowRecord.BorrowStatus status;
    private String notes;
    private LocalDateTime createdAt;
}
