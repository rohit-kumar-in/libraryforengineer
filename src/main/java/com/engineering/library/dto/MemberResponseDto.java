package com.engineering.library.dto;

import com.engineering.library.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "Member profile returned by the API")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponseDto {

    private Long id;
    private String fullName;
    private String rollNumber;
    private String email;
    private Member.MemberRole role;
    private Member.MemberStatus status;

    @Schema(description = "Number of books currently borrowed (max 5)")
    private Integer activeBorrowCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
