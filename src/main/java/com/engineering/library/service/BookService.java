package com.engineering.library.service;

import com.engineering.library.dto.BookRequestDto;
import com.engineering.library.dto.BookResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    BookResponseDto createBook(BookRequestDto request);

    BookResponseDto getBookById(Long id);

    BookResponseDto getBookByIsbn(String isbn);

    Page<BookResponseDto> searchByTitle(String title, Pageable pageable);

    Page<BookResponseDto> searchByKeyword(String keyword, Pageable pageable);

    Page<BookResponseDto> listAvailableBooks(Pageable pageable);

    Page<BookResponseDto> listAllBooks(Pageable pageable);

    BookResponseDto updateBook(Long id, BookRequestDto request);

    void deleteBook(Long id);
}
