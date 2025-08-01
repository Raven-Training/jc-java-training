package com.raven.training.service.impl;

import com.raven.training.exception.error.BookNotFoundException;
import com.raven.training.mapper.IBookMapper;
import com.raven.training.persistence.entity.Book;
import com.raven.training.persistence.repository.IBookRepository;
import com.raven.training.presentation.dto.book.BookRequest;
import com.raven.training.presentation.dto.book.BookResponse;
import com.raven.training.service.implementation.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for BookServiceImpl")
class BookServiceImplTest {

    @Mock
    private IBookRepository bookRepository;

    @Mock
    private IBookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookResponse bookResponse;
    private BookRequest bookRequest;
    private UUID bookId;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();
        book = Book.builder()
                .id(bookId)
                .title("Clean Code")
                .author("Robert C. Martin")
                .gender("Programming")
                .image("clean-code.jpg")
                .subtitle("A Handbook of Agile Software Craftsmanship")
                .publisher("Prentice Hall")
                .year("2008")
                .pages(464)
                .isbn("9780132350884")
                .build();

        bookResponse = new BookResponse(
                bookId,
                "Programming",
                "Robert C. Martin",
                "clean-code.jpg",
                "Clean Code",
                "A Handbook of Agile Software Craftsmanship",
                "Prentice Hall",
                "2008",
                464,
                "9780132350884"
        );

        bookRequest = new BookRequest(
                "Programming",
                "Robert C. Martin",
                "clean-code.jpg",
                "Clean Code",
                "A Handbook of Agile Software Craftsmanship",
                "Prentice Hall",
                "2008",
                464,
                "9780132350884"
        );
    }

    // =========================================
    // Tests for findAll()
    // =========================================

    @Test
    @DisplayName("Should return a page of book answers when there are no filters and books exist")
    void findAll_NoFilters_WhenBooksExist_ShouldReturnPageOfBookResponses() {
        List<Book> books = Arrays.asList(book, book);
        List<BookResponse> expectedResponses = Arrays.asList(bookResponse, bookResponse);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());
        Page<Book> booksPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(any(Pageable.class))).thenReturn(booksPage);
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        Page<BookResponse> resultPage = bookService.findAll(null, null, null, pageable);

        assertNotNull(resultPage, "The book answer page should not be null");
        assertFalse(resultPage.isEmpty(), "The page should not be empty");
        assertEquals(2, resultPage.getTotalElements(), "The total number of elements on the page should be 2");
        assertEquals(1, resultPage.getTotalPages(), "The total number of pages should be 1");
        assertEquals(0, resultPage.getNumber(), "The current page number should be 0");
        assertEquals(2, resultPage.getContent().size(), "The content of the page should have 2 books");
        assertEquals(expectedResponses, resultPage.getContent(), "The content of the page should match the expected responses");

        verify(bookRepository, times(1)).findAll(any(Pageable.class));
        verify(bookRepository, never()).findAllWithFilters(anyString(), anyString(), anyString(), any(Pageable.class));
        verify(bookMapper, times(books.size())).toResponse(any(Book.class));
    }

    @Test
    @DisplayName("Should return an empty page when there are no filters and no books exist")
    void findAll_NoFilters_WhenNoBooksExist_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> emptyBookPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(bookRepository.findAll(any(Pageable.class))).thenReturn(emptyBookPage);

        Page<BookResponse> resultPage = bookService.findAll(null, null, null, pageable);

        assertNotNull(resultPage, "The book answer page should not be null");
        assertTrue(resultPage.isEmpty(), "The page should be empty");
        assertEquals(0, resultPage.getTotalElements(), "The total number of elements on the page should be 0");
        assertEquals(0, resultPage.getTotalPages(), "The total number of pages should be 0");
        assertEquals(0, resultPage.getContent().size(), "The page content should have 0 books.");

        verify(bookRepository, times(1)).findAll(any(Pageable.class));
        verify(bookRepository, never()).findAllWithFilters(anyString(), anyString(), anyString(), any(Pageable.class));
        verify(bookMapper, never()).toResponse(any(Book.class));
    }

    @Test
    @DisplayName("Should return a page of book answers when a title filter is applied")
    void findAll_WithTitleFilter_ShouldReturnPageOfBookResponses() {
        String titleFilter = "Clean Code";
        List<Book> books = Arrays.asList(book);
        List<BookResponse> expectedResponses = Arrays.asList(bookResponse);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> booksPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAllWithFilters(eq(titleFilter), eq(""), eq(""), any(Pageable.class)))
                .thenReturn(booksPage);
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        Page<BookResponse> resultPage = bookService.findAll(titleFilter, null, null, pageable);

        assertNotNull(resultPage);
        assertFalse(resultPage.isEmpty());
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(expectedResponses, resultPage.getContent());

        verify(bookRepository, times(1)).findAllWithFilters(eq(titleFilter), eq(""), eq(""), any(Pageable.class));
        verify(bookRepository, never()).findAll(any(Pageable.class));
        verify(bookMapper, times(books.size())).toResponse(any(Book.class));
    }

    @Test
    @DisplayName("Should return a page of book answers when an author filter is applied")
    void findAll_WithAuthorFilter_ShouldReturnPageOfBookResponses() {
        String authorFilter = "Robert C. Martin";
        List<Book> books = Arrays.asList(book);
        List<BookResponse> expectedResponses = Arrays.asList(bookResponse);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> booksPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAllWithFilters(eq(""), eq(authorFilter), eq(""), any(Pageable.class)))
                .thenReturn(booksPage);
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        Page<BookResponse> resultPage = bookService.findAll(null, authorFilter, null, pageable);

        assertNotNull(resultPage);
        assertEquals(expectedResponses, resultPage.getContent());

        verify(bookRepository, times(1)).findAllWithFilters(eq(""), eq(authorFilter), eq(""), any(Pageable.class));
        verify(bookRepository, never()).findAll(any(Pageable.class));
        verify(bookMapper, times(books.size())).toResponse(any(Book.class));
    }

    @Test
    @DisplayName("Should return a page of book answers when a genre filter is applied")
    void findAll_WithGenderFilter_ShouldReturnPageOfBookResponses() {
        String genderFilter = "Programming";
        List<Book> books = Arrays.asList(book);
        List<BookResponse> expectedResponses = Arrays.asList(bookResponse);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> booksPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAllWithFilters(eq(""), eq(""), eq(genderFilter), any(Pageable.class)))
                .thenReturn(booksPage);
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        Page<BookResponse> resultPage = bookService.findAll(null, null, genderFilter, pageable);

        assertNotNull(resultPage);
        assertEquals(expectedResponses, resultPage.getContent());

        verify(bookRepository, times(1)).findAllWithFilters(eq(""), eq(""), eq(genderFilter), any(Pageable.class));
        verify(bookRepository, never()).findAll(any(Pageable.class));
        verify(bookMapper, times(books.size())).toResponse(any(Book.class));
    }

    @Test
    @DisplayName("Should return a page of book answers when multiple filters are applied")
    void findAll_WithMultipleFilters_ShouldReturnPageOfBookResponses() {
        String titleFilter = "Clean Code";
        String authorFilter = "Robert C. Martin";
        String genderFilter = "Programming";
        List<Book> books = Arrays.asList(book);
        List<BookResponse> expectedResponses = Arrays.asList(bookResponse);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> booksPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAllWithFilters(eq(titleFilter), eq(authorFilter), eq(genderFilter), any(Pageable.class)))
                .thenReturn(booksPage);
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        Page<BookResponse> resultPage = bookService.findAll(titleFilter, authorFilter, genderFilter, pageable);

        assertNotNull(resultPage);
        assertEquals(expectedResponses, resultPage.getContent());

        verify(bookRepository, times(1)).findAllWithFilters(eq(titleFilter), eq(authorFilter), eq(genderFilter), any(Pageable.class));
        verify(bookRepository, never()).findAll(any(Pageable.class));
        verify(bookMapper, times(books.size())).toResponse(any(Book.class));
    }

    @Test
    @DisplayName("Should return an empty page when filters do not find books")
    void findAll_WithFilters_WhenNoBooksFound_ShouldReturnEmptyPage() {
        String titleFilter = "NonExistentTitle";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> emptyBookPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(bookRepository.findAllWithFilters(eq(titleFilter), eq(""), eq(""), any(Pageable.class)))
                .thenReturn(emptyBookPage);

        Page<BookResponse> resultPage = bookService.findAll(titleFilter, null, null, pageable);

        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty());
        assertEquals(0, resultPage.getTotalElements());

        verify(bookRepository, times(1)).findAllWithFilters(eq(titleFilter), eq(""), eq(""), any(Pageable.class));
        verify(bookRepository, never()).findAll(any(Pageable.class));
        verify(bookMapper, never()).toResponse(any(Book.class));
    }

    @Test
    @DisplayName("You should call findAll without filters when the filters are empty strings")
    void findAll_WithEmptyFilters_ShouldCallFindAll() {
        String emptyTitle = "";
        String emptyAuthor = "";
        String emptyGender = "";
        List<Book> books = Arrays.asList(book);
        List<BookResponse> expectedResponses = Arrays.asList(bookResponse);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> booksPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(any(Pageable.class)))
                .thenReturn(booksPage);
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        Page<BookResponse> resultPage = bookService.findAll(emptyTitle, emptyAuthor, emptyGender, pageable);

        assertNotNull(resultPage);
        assertFalse(resultPage.isEmpty());
        assertEquals(expectedResponses, resultPage.getContent());

        verify(bookRepository, times(1)).findAll(any(Pageable.class));
        verify(bookRepository, never()).findAllWithFilters(anyString(), anyString(), anyString(), any(Pageable.class));
        verify(bookMapper, times(books.size())).toResponse(any(Book.class));
    }

    // =========================================
    // Tests for findById()
    // =========================================

    @Test
    @DisplayName("Should return a book when it exists with the provided ID")
    void findById_WhenBookExists_ShouldReturnBook() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        BookResponse result = bookService.findById(bookId);

        assertNotNull(result, "The book should not be null");
        assertEquals(bookId, result.id(), "The book ID should match");
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookMapper, times(1)).toResponse(book);
    }

    @Test
    @DisplayName("Should throw BookNotFoundException when the book does not exist")
    void findById_WhenBookNotExists_ShouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        when(bookRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.findById(nonExistentId),
                "Should throw BookNotFoundException");
        verify(bookRepository, times(1)).findById(nonExistentId);
        verify(bookMapper, never()).toResponse(any(Book.class));
    }

    // =========================================
    // Tests for save()
    // =========================================

    @Test
    @DisplayName("Should save and return a book when a valid request is provided")
    void save_WithValidRequest_ShouldSaveAndReturnBook() {
        when(bookMapper.toEntity(bookRequest)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        BookResponse result = bookService.save(bookRequest);

        assertNotNull(result, "The saved workbook should not be null");
        assertEquals(bookId, result.id(), "The saved workbook ID should match");
        verify(bookMapper, times(1)).toEntity(bookRequest);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toResponse(book);
    }

    // =========================================
    // Tests for update()
    // =========================================

    @Test
    @DisplayName("Should update and return a book when it exists with the provided ID")
    void update_WhenBookExists_ShouldUpdateAndReturnBook() {
        BookRequest updateRequest = new BookRequest(
                "Programming",
                "Robert C. Martin",
                null,
                "Clean Code 2",
                null,
                null,
                null,
                null,
                "9780132350884"
        );

        Book updatedBook = Book.builder()
                .id(bookId)
                .gender("Programming")
                .author("Robert C. Martin")
                .title("Clean Code 2")
                .isbn("9780132350884")
                .build();

        BookResponse updatedResponse = new BookResponse(
                bookId,
                "Programming",
                "Robert C. Martin",
                null,
                "Clean Code 2",
                null,
                null,
                null,
                null,
                "9780132350884"
        );

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);
        when(bookMapper.toResponse(any(Book.class))).thenReturn(updatedResponse);

        BookResponse result = bookService.update(bookId, updateRequest);

        assertNotNull(result, "The updated book should not be null");
        assertEquals("Clean Code 2", result.title(), "The title should be updated");
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookMapper, times(1)).toResponse(any(Book.class));
    }

    @Test
    @DisplayName("Should throw BookNotFoundException when updating a book that does not exist")
    void update_WhenBookNotExists_ShouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        when(bookRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class,
                () -> bookService.update(nonExistentId, bookRequest),
                "Should throw BookNotFoundException"
        );
        verify(bookRepository, times(1)).findById(nonExistentId);
        verify(bookRepository, never()).save(any(Book.class));
        verify(bookMapper, never()).toResponse(any(Book.class));
    }

    @Test
    @DisplayName("Should update all fields of an existing book when all valid fields are provided")
    void update_WhenBookExistsAndAllFieldsProvided_ShouldUpdateAllFields() {
        BookRequest fullUpdateRequest = new BookRequest(
                "Fiction",
                "Jane Austen",
                "pride-prejudice.jpg",
                "Pride and Prejudice",
                "A novel of manners",
                "T. Egerton",
                "1813",
                345,
                "9780141439518"
        );

        Book existingBookBeforeUpdate = Book.builder()
                .id(bookId)
                .title("Old Title")
                .author("Old Author")
                .gender("Old Genre")
                .image("old-image.jpg")
                .subtitle("Old Subtitle")
                .publisher("Old Publisher")
                .year("2000")
                .pages(100)
                .isbn("1111111111111")
                .build();

        BookResponse expectedResponse = new BookResponse(
                bookId,
                fullUpdateRequest.gender(),
                fullUpdateRequest.author(),
                fullUpdateRequest.image(),
                fullUpdateRequest.title(),
                fullUpdateRequest.subtitle(),
                fullUpdateRequest.publisher(),
                fullUpdateRequest.year(),
                fullUpdateRequest.pages(),
                fullUpdateRequest.isbn()
        );

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBookBeforeUpdate));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book savedBook = invocation.getArgument(0);
            assertEquals(fullUpdateRequest.gender(), savedBook.getGender(), "Gender should be updated");
            assertEquals(fullUpdateRequest.author(), savedBook.getAuthor(), "Author should be updated");
            assertEquals(fullUpdateRequest.image(), savedBook.getImage(), "Image should be updated");
            assertEquals(fullUpdateRequest.title(), savedBook.getTitle(), "Title should be updated");
            assertEquals(fullUpdateRequest.subtitle(), savedBook.getSubtitle(), "Subtitle should be updated");
            assertEquals(fullUpdateRequest.publisher(), savedBook.getPublisher(), "Publisher should be updated");
            assertEquals(fullUpdateRequest.year(), savedBook.getYear(), "Year should be updated");
            assertEquals(fullUpdateRequest.pages(), savedBook.getPages(), "Pages should be updated");
            assertEquals(fullUpdateRequest.isbn(), savedBook.getIsbn(), "ISBN should be updated");
            return savedBook;
        });
        when(bookMapper.toResponse(any(Book.class))).thenReturn(expectedResponse);

        BookResponse result = bookService.update(bookId, fullUpdateRequest);

        assertNotNull(result, "The response should not be null");
        assertEquals(expectedResponse.title(), result.title(), "Returned title should match updated title");
        assertEquals(expectedResponse.author(), result.author(), "Returned author should match updated author");
        assertEquals(expectedResponse.gender(), result.gender(), "Returned gender should match updated gender");
        assertEquals(expectedResponse.image(), result.image(), "Returned image should match updated image");
        assertEquals(expectedResponse.subtitle(), result.subtitle(), "Returned subtitle should match updated subtitle");
        assertEquals(expectedResponse.publisher(), result.publisher(), "Returned publisher should match updated publisher");
        assertEquals(expectedResponse.year(), result.year(), "Returned year should match updated year");
        assertEquals(expectedResponse.pages(), result.pages(), "Returned pages should match updated pages");
        assertEquals(expectedResponse.isbn(), result.isbn(), "Returned isbn should match updated isbn");

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookMapper, times(1)).toResponse(any(Book.class));
    }

    @Test
    @DisplayName("Should update only provided fields when some fields in request are null or empty")
    void update_WhenBookExistsAndSomeFieldsNullOrEmpty_ShouldUpdateOnlyProvided() {
        BookRequest partialUpdateRequest = new BookRequest(
                "New Gender",
                null,
                "",
                "New Title",
                null,
                " ",
                null,
                null,
                " "
        );

        Book originalBook = Book.builder()
                .id(bookId)
                .gender("Old Gender")
                .author("Original Author")
                .image("original-image.jpg")
                .title("Original Title")
                .subtitle("Original Subtitle")
                .publisher("Original Publisher")
                .year("1990")
                .pages(200)
                .isbn("9999999999999")
                .build();

        BookResponse expectedResponse = new BookResponse(
                bookId,
                "New Gender",
                "Original Author",
                "original-image.jpg",
                "New Title",
                "Original Subtitle",
                "Original Publisher",
                "1990",
                200,
                "9999999999999"
        );

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(originalBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book savedBook = invocation.getArgument(0);
            assertEquals(partialUpdateRequest.gender(), savedBook.getGender(), "Gender should be updated");
            assertEquals(partialUpdateRequest.title(), savedBook.getTitle(), "Title should be updated");

            assertEquals("Original Author", savedBook.getAuthor(), "Author should remain unchanged");
            assertEquals("original-image.jpg", savedBook.getImage(), "Image should remain unchanged");
            assertEquals("Original Subtitle", savedBook.getSubtitle(), "Subtitle should remain unchanged");
            assertEquals("Original Publisher", savedBook.getPublisher(), "Publisher should remain unchanged");
            assertEquals("1990", savedBook.getYear(), "Year should remain unchanged");
            assertEquals(200, savedBook.getPages(), "Pages should remain unchanged");
            assertEquals("9999999999999", savedBook.getIsbn(), "ISBN should remain unchanged");

            return savedBook;
        });
        when(bookMapper.toResponse(any(Book.class))).thenReturn(expectedResponse);

        BookResponse result = bookService.update(bookId, partialUpdateRequest);

        assertNotNull(result);
        assertEquals("New Gender", result.gender());
        assertEquals("New Title", result.title());
        assertEquals("Original Author", result.author());
        assertEquals("original-image.jpg", result.image());

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookMapper, times(1)).toResponse(any(Book.class));
    }

    // =========================================
    // Tests for delete()
    // =========================================

    @Test
    @DisplayName("You should delete a book when it exists with the provided ID")
    void delete_WhenBookExists_ShouldDeleteBook() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).delete(book);

        assertDoesNotThrow(() -> bookService.delete(bookId),
                "Should not throw any exceptions");
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    @DisplayName("Should throw BookNotFoundException when trying to delete a book that does not exist")
    void delete_WhenBookNotExists_ShouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        when(bookRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class,
                () -> bookService.delete(nonExistentId),
                "Should throw BookNotFoundException"
        );
        verify(bookRepository, times(1)).findById(nonExistentId);
        verify(bookRepository, never()).delete(any(Book.class));
    }
}
