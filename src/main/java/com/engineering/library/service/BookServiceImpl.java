package com.engineering.library.service;

import com.engineering.library.dto.BookRequestDto;
import com.engineering.library.dto.BookResponseDto;
import com.engineering.library.entity.Book;
import com.engineering.library.exception.BookNotFoundException;
import com.engineering.library.exception.DuplicateResourceException;
import com.engineering.library.mapper.BookMapper;
import com.engineering.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper     bookMapper;

    @Override
    @Transactional
    public BookResponseDto createBook(BookRequestDto request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateResourceException(
                "A book with ISBN " + request.getIsbn() + " already exists."
            );
        }

        Book book = bookMapper.toEntity(request);
        // On creation, all copies start as available
        book.setAvailableCopies(request.getTotalCopies());
        book.setIsAvailable(request.getTotalCopies() > 0);

        Book saved = bookRepository.save(book);
        log.info("Created book [id={}] ISBN={}", saved.getId(), saved.getIsbn());
        return bookMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDto getBookById(Long id) {
        return bookMapper.toResponseDto(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDto getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
            .orElseThrow(() -> new BookNotFoundException(isbn));
        return bookMapper.toResponseDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponseDto> searchByTitle(String title, Pageable pageable) {
        return bookRepository
            .findByTitleContainingIgnoreCaseOrderByEditionDesc(title, pageable)
            .map(bookMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponseDto> searchByKeyword(String keyword, Pageable pageable) {
        return bookRepository
            .searchByKeyword(keyword, pageable)
            .map(bookMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponseDto> listAvailableBooks(Pageable pageable) {
        return bookRepository.findByIsAvailableTrue(pageable)
            .map(bookMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponseDto> listAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable).map(bookMapper::toResponseDto);
    }

    @Override
    @Transactional
    public BookResponseDto updateBook(Long id, BookRequestDto request) {
        Book existing = findOrThrow(id);

        // Guard: if ISBN changed, ensure new ISBN is not taken
        if (!existing.getIsbn().equals(request.getIsbn())
                && bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateResourceException(
                "A book with ISBN " + request.getIsbn() + " already exists."
            );
        }

        bookMapper.updateEntityFromDto(request, existing);
        // Re-derive availability from the updated totalCopies
        int delta = request.getTotalCopies() - existing.getTotalCopies();
        existing.setAvailableCopies(Math.max(0, existing.getAvailableCopies() + delta));
        existing.setIsAvailable(existing.getAvailableCopies() > 0);

        Book saved = bookRepository.save(existing);
        log.info("Updated book [id={}]", saved.getId());
        return bookMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = findOrThrow(id);
        bookRepository.delete(book);
        log.info("Deleted book [id={}]", id);
    }

    /* ─── helpers ─── */

    private Book findOrThrow(Long id) {
        return bookRepository.findById(id)
            .orElseThrow(() -> new BookNotFoundException(id));
    }
}
