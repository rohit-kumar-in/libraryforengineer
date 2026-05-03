package com.engineering.library.mapper;

import com.engineering.library.dto.BorrowRecordResponseDto;
import com.engineering.library.entity.Book;
import com.engineering.library.entity.BorrowRecord;
import com.engineering.library.entity.Member;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-03T23:21:10+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class BorrowRecordMapperImpl implements BorrowRecordMapper {

    @Override
    public BorrowRecordResponseDto toResponseDto(BorrowRecord record) {
        if ( record == null ) {
            return null;
        }

        BorrowRecordResponseDto.BorrowRecordResponseDtoBuilder borrowRecordResponseDto = BorrowRecordResponseDto.builder();

        borrowRecordResponseDto.memberId( recordMemberId( record ) );
        borrowRecordResponseDto.memberName( recordMemberFullName( record ) );
        borrowRecordResponseDto.memberRollNumber( recordMemberRollNumber( record ) );
        borrowRecordResponseDto.bookId( recordBookId( record ) );
        borrowRecordResponseDto.bookTitle( recordBookTitle( record ) );
        borrowRecordResponseDto.bookIsbn( recordBookIsbn( record ) );
        borrowRecordResponseDto.borrowDate( record.getBorrowDate() );
        borrowRecordResponseDto.createdAt( record.getCreatedAt() );
        borrowRecordResponseDto.dueDate( record.getDueDate() );
        borrowRecordResponseDto.id( record.getId() );
        borrowRecordResponseDto.notes( record.getNotes() );
        borrowRecordResponseDto.returnDate( record.getReturnDate() );
        borrowRecordResponseDto.status( record.getStatus() );

        return borrowRecordResponseDto.build();
    }

    private Long recordMemberId(BorrowRecord borrowRecord) {
        if ( borrowRecord == null ) {
            return null;
        }
        Member member = borrowRecord.getMember();
        if ( member == null ) {
            return null;
        }
        Long id = member.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String recordMemberFullName(BorrowRecord borrowRecord) {
        if ( borrowRecord == null ) {
            return null;
        }
        Member member = borrowRecord.getMember();
        if ( member == null ) {
            return null;
        }
        String fullName = member.getFullName();
        if ( fullName == null ) {
            return null;
        }
        return fullName;
    }

    private String recordMemberRollNumber(BorrowRecord borrowRecord) {
        if ( borrowRecord == null ) {
            return null;
        }
        Member member = borrowRecord.getMember();
        if ( member == null ) {
            return null;
        }
        String rollNumber = member.getRollNumber();
        if ( rollNumber == null ) {
            return null;
        }
        return rollNumber;
    }

    private Long recordBookId(BorrowRecord borrowRecord) {
        if ( borrowRecord == null ) {
            return null;
        }
        Book book = borrowRecord.getBook();
        if ( book == null ) {
            return null;
        }
        Long id = book.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String recordBookTitle(BorrowRecord borrowRecord) {
        if ( borrowRecord == null ) {
            return null;
        }
        Book book = borrowRecord.getBook();
        if ( book == null ) {
            return null;
        }
        String title = book.getTitle();
        if ( title == null ) {
            return null;
        }
        return title;
    }

    private String recordBookIsbn(BorrowRecord borrowRecord) {
        if ( borrowRecord == null ) {
            return null;
        }
        Book book = borrowRecord.getBook();
        if ( book == null ) {
            return null;
        }
        String isbn = book.getIsbn();
        if ( isbn == null ) {
            return null;
        }
        return isbn;
    }
}
