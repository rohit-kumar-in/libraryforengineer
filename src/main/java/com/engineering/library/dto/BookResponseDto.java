package com.engineering.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Read-only projection of a {@link com.engineering.library.entity.Book}.
 * Never exposes internal DB fields directly.
 */
@Schema(description = "Book details returned by the API")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponseDto {

    @Schema(description = "Unique identifier", example = "1")
    private Long id;

    private String title;
    private String author;
    private String isbn;

    @Schema(description = "Edition number", example = "4")
    private Integer edition;

    private String callNumber;
    private String publisher;
    private Integer publicationYear;
    private Integer totalCopies;
    private Integer availableCopies;

    @Schema(description = "True when at least one copy is on the shelf")
    private Boolean isAvailable;

    private String subject;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
