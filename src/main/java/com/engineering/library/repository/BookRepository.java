package com.engineering.library.repository;

import com.engineering.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    /**
     * Full-title search, results ordered by edition DESC so the latest
     * edition is always at the top — satisfying the "Strict Edition Search"
     * requirement.
     *
     * <p>JPQL LOWER() makes the search case-insensitive without a custom
     * DB function.</p>
     */
    @Query("""
        SELECT b FROM Book b
        WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))
        ORDER BY b.edition DESC
        """)
    Page<Book> findByTitleContainingIgnoreCaseOrderByEditionDesc(
        @Param("title") String title,
        Pageable pageable
    );

    /**
     * Multi-field search across title, author, and subject, ordered by
     * edition DESC.
     */
    @Query("""
        SELECT b FROM Book b
        WHERE LOWER(b.title)   LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(b.author)  LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(b.subject) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY b.edition DESC
        """)
    Page<Book> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Retrieves all books that have at least one copy available.
     */
    Page<Book> findByIsAvailableTrue(Pageable pageable);
}
