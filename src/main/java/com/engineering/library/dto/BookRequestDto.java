package com.engineering.library.dto;

import com.engineering.library.entity.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO used when creating or updating a {@link Book}.
 * Entities are never exposed directly to the API layer.
 */
@Schema(description = "Payload for creating or updating a book")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequestDto {

    @Schema(description = "Book title", example = "Introduction to Algorithms")
    @NotBlank(message = "Title must not be blank")
    @Size(max = 255)
    private String title;

    @Schema(description = "Author(s)", example = "Cormen, Leiserson, Rivest, Stein")
    @NotBlank(message = "Author must not be blank")
    @Size(max = 255)
    private String author;

    @Schema(
        description = "ISBN-13 without hyphens (starts with 978 or 979)",
        example = "9780262033848"
    )
    @NotBlank(message = "ISBN must not be blank")
    @Pattern(
        regexp = "^(978|979)\\d{10}$",
        message = "ISBN must be a valid 13-digit ISBN-13"
    )
    private String isbn;

    @Schema(description = "Edition number (positive integer)", example = "4")
    @NotNull(message = "Edition is required")
    @Min(value = 1, message = "Edition must be ≥ 1")
    @Max(value = 99,  message = "Edition must be ≤ 99")
    private Integer edition;

    @Schema(description = "Library call number", example = "QA76.9.A43 C67 2022")
    @Size(max = 50)
    private String callNumber;

    @Schema(example = "MIT Press")
    @Size(max = 255)
    private String publisher;

    @Schema(description = "Year of publication", example = "2022")
    @Min(1800) @Max(2100)
    private Integer publicationYear;

    @Schema(description = "Total physical copies", example = "5")
    @NotNull
    @Min(value = 1, message = "At least one copy must be registered")
    private Integer totalCopies;

    @Schema(description = "Subject / genre", example = "Computer Science")
    @Size(max = 255)
    private String subject;
}
