package com.engineering.library.mapper;

import com.engineering.library.dto.BorrowRecordResponseDto;
import com.engineering.library.entity.BorrowRecord;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BorrowRecordMapper {

    @Mapping(source = "member.id",         target = "memberId")
    @Mapping(source = "member.fullName",   target = "memberName")
    @Mapping(source = "member.rollNumber", target = "memberRollNumber")
    @Mapping(source = "book.id",           target = "bookId")
    @Mapping(source = "book.title",        target = "bookTitle")
    @Mapping(source = "book.isbn",         target = "bookIsbn")
    BorrowRecordResponseDto toResponseDto(BorrowRecord record);
}
