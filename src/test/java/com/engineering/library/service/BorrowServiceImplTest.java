package com.engineering.library.service;

import com.engineering.library.dto.BorrowRecordResponseDto;
import com.engineering.library.entity.*;
import com.engineering.library.entity.BorrowRecord.BorrowStatus;
import com.engineering.library.exception.*;
import com.engineering.library.mapper.BorrowRecordMapper;
import com.engineering.library.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link BorrowServiceImpl}.
 *
 * <p>Uses Mockito — no Spring context, no DB — for fast, isolated testing.</p>
 */
@ExtendWith(MockitoExtension.class)
class BorrowServiceImplTest {

    @Mock private BorrowRecordRepository borrowRepo;
    @Mock private BookRepository         bookRepo;
    @Mock private MemberRepository       memberRepo;
    @Mock private BorrowRecordMapper     borrowMapper;

    @InjectMocks
    private BorrowServiceImpl borrowService;

    private Member activeMember;
    private Book   availableBook;

    @BeforeEach
    void setUp() {
        // Inject @Value fields manually (no Spring context)
        org.springframework.test.util.ReflectionTestUtils.setField(borrowService, "maxBooksPerMember", 5);
        org.springframework.test.util.ReflectionTestUtils.setField(borrowService, "loanDurationDays", 14);

        activeMember = Member.builder()
            .id(1L)
            .fullName("Alice Kumar")
            .rollNumber("CS2024001")
            .email("alice@engineering.edu")
            .status(Member.MemberStatus.ACTIVE)
            .activeBorrowCount(0)
            .build();

        availableBook = Book.builder()
            .id(42L)
            .title("Introduction to Algorithms")
            .isbn("9780262033848")
            .edition(4)
            .totalCopies(3)
            .availableCopies(3)
            .isAvailable(true)
            .build();
    }

    /* ─────────────── Happy path ─────────────── */

    @Test
    @DisplayName("borrowBook() — happy path creates a BorrowRecord")
    void borrowBook_happyPath() {
        when(memberRepo.findById(1L)).thenReturn(Optional.of(activeMember));
        when(bookRepo.findById(42L)).thenReturn(Optional.of(availableBook));
        when(borrowRepo.countByMemberIdAndStatus(1L, BorrowStatus.ACTIVE)).thenReturn(0L);
        when(borrowRepo.existsByMemberIdAndBookIdAndStatus(1L, 42L, BorrowStatus.ACTIVE))
            .thenReturn(false);
        when(borrowRepo.save(any(BorrowRecord.class))).thenAnswer(inv -> inv.getArgument(0));
        when(borrowMapper.toResponseDto(any())).thenReturn(new BorrowRecordResponseDto());

        BorrowRecordResponseDto result = borrowService.borrowBook(1L, 42L);

        assertThat(result).isNotNull();
        // availableCopies should have been decremented
        assertThat(availableBook.getAvailableCopies()).isEqualTo(2);
        assertThat(availableBook.getIsAvailable()).isTrue();
        // member borrow count incremented
        assertThat(activeMember.getActiveBorrowCount()).isEqualTo(1);

        verify(borrowRepo).save(any(BorrowRecord.class));
    }

    /* ─────────────── Guard: book not available ─────────────── */

    @Test
    @DisplayName("borrowBook() — throws BookNotAvailableException when no copies left")
    void borrowBook_bookNotAvailable() {
        availableBook.setAvailableCopies(0);
        availableBook.setIsAvailable(false);

        when(memberRepo.findById(1L)).thenReturn(Optional.of(activeMember));
        when(bookRepo.findById(42L)).thenReturn(Optional.of(availableBook));

        assertThatThrownBy(() -> borrowService.borrowBook(1L, 42L))
            .isInstanceOf(BookNotAvailableException.class)
            .hasMessageContaining("42");
    }

    /* ─────────────── Guard: borrow limit exceeded ─────────────── */

    @Test
    @DisplayName("borrowBook() — throws BorrowLimitExceededException at limit=5")
    void borrowBook_limitExceeded() {
        when(memberRepo.findById(1L)).thenReturn(Optional.of(activeMember));
        when(bookRepo.findById(42L)).thenReturn(Optional.of(availableBook));
        when(borrowRepo.countByMemberIdAndStatus(1L, BorrowStatus.ACTIVE)).thenReturn(5L);

        assertThatThrownBy(() -> borrowService.borrowBook(1L, 42L))
            .isInstanceOf(BorrowLimitExceededException.class)
            .hasMessageContaining("maximum borrow limit");
    }

    /* ─────────────── Guard: member not found ─────────────── */

    @Test
    @DisplayName("borrowBook() — throws MemberNotFoundException for unknown member")
    void borrowBook_memberNotFound() {
        when(memberRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowService.borrowBook(99L, 42L))
            .isInstanceOf(MemberNotFoundException.class);
    }

    /* ─────────────── Guard: suspended member ─────────────── */

    @Test
    @DisplayName("borrowBook() — throws IllegalStateException for suspended member")
    void borrowBook_suspendedMember() {
        activeMember.setStatus(Member.MemberStatus.SUSPENDED);
        when(memberRepo.findById(1L)).thenReturn(Optional.of(activeMember));
        when(bookRepo.findById(42L)).thenReturn(Optional.of(availableBook));

        assertThatThrownBy(() -> borrowService.borrowBook(1L, 42L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("not active");
    }

    /* ─────────────── Return ─────────────── */

    @Test
    @DisplayName("returnBook() — restores availability and marks record RETURNED")
    void returnBook_happyPath() {
        availableBook.setAvailableCopies(2); // one copy already out
        BorrowRecord activeRecord = BorrowRecord.builder()
            .id(100L)
            .member(activeMember)
            .book(availableBook)
            .status(BorrowStatus.ACTIVE)
            .borrowDate(java.time.LocalDate.now().minusDays(5))
            .dueDate(java.time.LocalDate.now().plusDays(9))
            .build();

        activeMember.setActiveBorrowCount(1);

        when(borrowRepo.findByMemberIdAndBookIdAndStatus(1L, 42L, BorrowStatus.ACTIVE))
            .thenReturn(Optional.of(activeRecord));
        when(borrowRepo.save(any(BorrowRecord.class))).thenAnswer(inv -> inv.getArgument(0));
        when(borrowMapper.toResponseDto(any())).thenReturn(new BorrowRecordResponseDto());

        borrowService.returnBook(1L, 42L);

        assertThat(activeRecord.getStatus()).isEqualTo(BorrowStatus.RETURNED);
        assertThat(activeRecord.getReturnDate()).isEqualTo(java.time.LocalDate.now());
        assertThat(availableBook.getAvailableCopies()).isEqualTo(3);
        assertThat(activeMember.getActiveBorrowCount()).isEqualTo(0);
    }
}
