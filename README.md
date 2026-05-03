# Library Management System — Architecture Reference

## Tech Stack

| Layer | Technology |
|---|---|
| Runtime | Java 17 / Spring Boot 3.3 |
| Web | Spring MVC (Servlet stack) |
| Data | Spring Data JPA + Hibernate + PostgreSQL |
| Validation | Hibernate Validator (Bean Validation 3.0) |
| Mapping | MapStruct 1.5.5 (compile-time, Spring component model) |
| Docs | SpringDoc OpenAPI 2.5 → Swagger UI |
| Boilerplate | Lombok |
| Testing | JUnit 5 + Mockito + AssertJ |

---

## Package Structure

```
com.engineering.library
├── LibraryManagementApplication.java   ← entry point
│
├── entity/
│   ├── Book.java              ← ISBN-13 validated, edition-indexed
│   ├── Member.java            ← STUDENT | FACULTY | STAFF, status, borrow counter
│   └── BorrowRecord.java      ← ACTIVE → RETURNED | OVERDUE lifecycle
│
├── dto/
│   ├── BookRequestDto.java    ← @Valid input surface for book CRUD
│   ├── BookResponseDto.java   ← safe read projection
│   ├── MemberRequestDto/ResponseDto.java
│   └── BorrowRecordResponseDto.java
│
├── mapper/                    ← MapStruct interfaces (generated at compile time)
│   ├── BookMapper.java
│   ├── MemberMapper.java
│   └── BorrowRecordMapper.java
│
├── repository/
│   ├── BookRepository.java    ← custom JPQL: edition-DESC search
│   ├── MemberRepository.java
│   └── BorrowRecordRepository.java
│
├── service/
│   ├── BookService + BookServiceImpl.java
│   ├── BorrowService + BorrowServiceImpl.java   ← ★ core workflow
│   └── MemberService.java
│
├── controller/
│   ├── BookController.java    ← /books/**
│   ├── BorrowController.java  ← /borrows/**
│   └── MemberController.java  ← /members/**
│
└── exception/
    ├── BookNotFoundException.java
    ├── MemberNotFoundException.java
    ├── BorrowLimitExceededException.java
    ├── BookNotAvailableException.java
    ├── DuplicateResourceException.java
    └── GlobalExceptionHandler.java   ← @RestControllerAdvice
```

---

## Key Design Decisions

### 1 — DTO Pattern (Entity ≠ API)
Entities are **never** returned directly. MapStruct generates the converter code at compile time (zero reflection overhead).

### 2 — Strict Edition Search
```sql
SELECT b FROM Book b
WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))
ORDER BY b.edition DESC
```
The latest edition always surfaces first.

### 3 — Transactional Borrow Workflow
`BorrowServiceImpl.borrowBook()` runs inside a **single `@Transactional` boundary**:

```
1. Validate member is ACTIVE
2. Validate book exists
3. Check availableCopies > 0      → BookNotAvailableException (409)
4. Count active borrows ≤ 5       → BorrowLimitExceededException (422)
5. Check no duplicate active borrow
6. book.decrementAvailable()
7. member.activeBorrowCount++
8. borrowRepo.save(new record)
```
If any step throws, the entire transaction rolls back.

### 4 — Global Exception Handler
```json
{
  "timestamp": "2024-06-01T10:30:00",
  "status":    404,
  "error":     "Not Found",
  "message":   "Book not found with id: 42",
  "path":      "/api/v1/books/42"
}
```
Validation errors also include a `fieldErrors` map.

---

## API Endpoints

All endpoints are prefixed with `/api/v1` (set in `application.yml`).

### Books — `/books`

| Method | Path | Description |
|---|---|---|
| `POST` | `/books` | Register a book |
| `GET` | `/books/{id}` | Get by ID |
| `GET` | `/books/isbn/{isbn}` | Get by ISBN-13 |
| `GET` | `/books/search/title?q=` | **Title search — edition DESC** |
| `GET` | `/books/search?q=` | Keyword search (title/author/subject) — edition DESC |
| `GET` | `/books` | List all (paginated) |
| `GET` | `/books/available` | List available only |
| `PUT` | `/books/{id}` | Update book |
| `DELETE` | `/books/{id}` | Delete book |

### Members — `/members`

| Method | Path | Description |
|---|---|---|
| `POST` | `/members` | Register member |
| `GET` | `/members/{id}` | Get member |
| `GET` | `/members` | List all |
| `PUT` | `/members/{id}` | Update profile |
| `PATCH` | `/members/{id}/suspend` | Suspend member |

### Borrowing — `/borrows`

| Method | Path | Description |
|---|---|---|
| `POST` | `/borrows/borrow?memberId=&bookId=` | **Borrow a book** |
| `POST` | `/borrows/return?memberId=&bookId=` | **Return a book** |
| `GET` | `/borrows/member/{memberId}` | Member's borrow history |
| `GET` | `/borrows/book/{bookId}` | Book's borrow history |

---

## Quick Start

### Prerequisites
- Java 17+, Maven 3.9+
- PostgreSQL running on `localhost:5432`

### 1 — Create the database
```sql
CREATE DATABASE library_db;
```

### 2 — Configure credentials
Either set environment variables:
```powershell
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "yourpassword"
```
Or edit `src/main/resources/application.yml` directly.

### 3 — Run
```powershell
cd c:\Users\rkumar72\Downloads\project\library
mvn spring-boot:run
```

### 4 — Open Swagger UI
```
http://localhost:8080/api/v1/swagger-ui.html
```

### 5 — Run Tests
```powershell
mvn test
```

---

## ISBN-13 Validation Rule
```
^(978|979)\d{10}$
```
- Must be exactly 13 digits
- Must start with `978` or `979`
- Stored without hyphens

> [!TIP]
> For production, add a Flyway or Liquibase migration to manage schema changes instead of relying on `ddl-auto: update`.

> [!WARNING]
> Change `ddl-auto` from `update` to `validate` after initial setup to prevent accidental schema mutations in production.
