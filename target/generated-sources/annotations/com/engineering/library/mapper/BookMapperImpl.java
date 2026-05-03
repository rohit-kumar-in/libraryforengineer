package com.engineering.library.mapper;

import com.engineering.library.dto.BookRequestDto;
import com.engineering.library.dto.BookResponseDto;
import com.engineering.library.entity.Book;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-03T23:21:10+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class BookMapperImpl implements BookMapper {

    @Override
    public Book toEntity(BookRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Book.BookBuilder book = Book.builder();

        book.author( dto.getAuthor() );
        book.callNumber( dto.getCallNumber() );
        book.edition( dto.getEdition() );
        book.isbn( dto.getIsbn() );
        book.publicationYear( dto.getPublicationYear() );
        book.publisher( dto.getPublisher() );
        book.subject( dto.getSubject() );
        book.title( dto.getTitle() );
        book.totalCopies( dto.getTotalCopies() );

        return book.build();
    }

    @Override
    public BookResponseDto toResponseDto(Book book) {
        if ( book == null ) {
            return null;
        }

        BookResponseDto.BookResponseDtoBuilder bookResponseDto = BookResponseDto.builder();

        bookResponseDto.author( book.getAuthor() );
        bookResponseDto.availableCopies( book.getAvailableCopies() );
        bookResponseDto.callNumber( book.getCallNumber() );
        bookResponseDto.createdAt( book.getCreatedAt() );
        bookResponseDto.edition( book.getEdition() );
        bookResponseDto.id( book.getId() );
        bookResponseDto.isAvailable( book.getIsAvailable() );
        bookResponseDto.isbn( book.getIsbn() );
        bookResponseDto.publicationYear( book.getPublicationYear() );
        bookResponseDto.publisher( book.getPublisher() );
        bookResponseDto.subject( book.getSubject() );
        bookResponseDto.title( book.getTitle() );
        bookResponseDto.totalCopies( book.getTotalCopies() );
        bookResponseDto.updatedAt( book.getUpdatedAt() );

        return bookResponseDto.build();
    }

    @Override
    public void updateEntityFromDto(BookRequestDto dto, Book book) {
        if ( dto == null ) {
            return;
        }

        book.setAuthor( dto.getAuthor() );
        book.setCallNumber( dto.getCallNumber() );
        book.setEdition( dto.getEdition() );
        book.setIsbn( dto.getIsbn() );
        book.setPublicationYear( dto.getPublicationYear() );
        book.setPublisher( dto.getPublisher() );
        book.setSubject( dto.getSubject() );
        book.setTitle( dto.getTitle() );
        book.setTotalCopies( dto.getTotalCopies() );
    }
}
