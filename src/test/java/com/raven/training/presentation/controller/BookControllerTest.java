package com.raven.training.presentation.controller;

import com.raven.training.persistence.repository.IBookRepository;
import com.raven.training.presentation.dto.book.BookRequest;
import com.raven.training.presentation.dto.book.BookResponse;
import com.raven.training.presentation.dto.book.bookexternal.BookResponseDTO;
import com.raven.training.presentation.dto.pagination.CustomPageableResponse;
import com.raven.training.service.implementation.OpenLibraryService;
import com.raven.training.service.interfaces.IBookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for BookController")
class BookControllerTest {

    @Mock
    private IBookService bookService;

    @Mock
    private OpenLibraryService openLibraryService;

    @Mock
    private IBookRepository bookRepository;

    @InjectMocks
    private BookController controller;

    @InjectMocks
    private BookController bookController;


    private BookRequest bookRequest;
    private BookResponse bookResponse;
    private final UUID bookId = UUID.randomUUID();
    private static final String ISBN = "9780132350884";

    @BeforeEach
    void setUp() {
        bookRequest = new BookRequest(
                "Programming",
                "Robert C. Martin",
                "clean_code.jpg",
                "Clean Code",
                "A Handbook of Agile Software Craftsmanship",
                "Prentice Hall",
                "2008",
                464,
                "9780132350884"
        );

        bookResponse = new BookResponse(
                bookId,
                "Programming",
                "Robert C. Martin",
                "clean_code.jpg",
                "Clean Code",
                "A Handbook of Agile Software Craftsmanship",
                "Prentice Hall",
                "2008",
                464,
                "9780132350884"
        );
    }

    @Test
    @DisplayName("Should return a page of book responses when no filters are submitted and books exist")
    void findAll_NoFilters_WhenBooksExist_ShouldReturnPageOfBookResponses() {
        List<BookResponse> bookResponses = Arrays.asList(bookResponse, bookResponse);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<BookResponse> expectedPage = new PageImpl<>(bookResponses, pageable, bookResponses.size());

        when(bookService.findAll(eq(null), eq(null), eq(null), any(Pageable.class))).thenReturn(expectedPage);

        ResponseEntity<CustomPageableResponse<BookResponse>> response = bookController.findAll(0, 10, null, null, null);

        assertNotNull(response, "The response should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "The status code should be 200 OK");

        CustomPageableResponse<BookResponse> result = response.getBody();
        assertNotNull(result, "The response body should not be null");

        assertFalse(result.page().isEmpty(), "The result page should not be empty");
        assertEquals(2, result.page().size(), "The page should contain 2 books");
        assertEquals(2, result.total_count(), "The total count should be 2");
        assertEquals(1, result.total_pages(), "The total pages should be 1");
        assertEquals(10, result.limit(), "The limit should be 10");
        assertEquals(1, result.current_page(), "The current page should be 1");
        assertNull(result.previous_page(), "The previous page should be null for the first page");
        assertNull(result.next_page(), "The next page should be null for the last page");

        verify(bookService, times(1)).findAll(eq(null), eq(null), eq(null), any(Pageable.class));
    }

    @Test
    @DisplayName("Should return an empty page when no filters are sent and no books exist")
    void findAll_NoFilters_WhenNoBooksExist_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<BookResponse> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(bookService.findAll(eq(null), eq(null), eq(null), any(Pageable.class))).thenReturn(emptyPage);

        ResponseEntity<CustomPageableResponse<BookResponse>> response = bookController.findAll(0, 10, null, null, null);

        assertNotNull(response, "The response should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "The status code should be 200 OK");

        CustomPageableResponse<BookResponse> result = response.getBody();
        assertNotNull(result, "The response body should not be null");

        assertTrue(result.page().isEmpty(), "The result page should be empty");
        assertEquals(0, result.page().size(), "The page should contain 0 books");
        assertEquals(0, result.total_count(), "The total count should be 0");
        assertEquals(0, result.total_pages(), "The total pages should be 0");
        assertEquals(10, result.limit(), "The limit should be 10");
        assertEquals(1, result.current_page(), "The current page should be 1");
        assertNull(result.previous_page(), "The previous page should be null");
        assertNull(result.next_page(), "The next page should be null");

        verify(bookService, times(1)).findAll(eq(null), eq(null), eq(null), any(Pageable.class));
    }

    @Test
    @DisplayName("Should return a page of book answers when a title filter is submitted")
    void findAll_WithTitleFilter_ShouldReturnPageOfBookResponses() {
        String titleFilter = "Clean Code";
        List<BookResponse> bookResponses = Arrays.asList(bookResponse);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<BookResponse> expectedPage = new PageImpl<>(bookResponses, pageable, bookResponses.size());

        when(bookService.findAll(eq(titleFilter), eq(null), eq(null), any(Pageable.class))).thenReturn(expectedPage);

        ResponseEntity<CustomPageableResponse<BookResponse>> response = bookController.findAll(0, 10, titleFilter, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CustomPageableResponse<BookResponse> result = response.getBody();
        assertNotNull(result);

        assertFalse(result.page().isEmpty(), "The result page should not be empty");
        assertEquals(1, result.page().size(), "The page should contain 1 book");
        assertEquals(1, result.total_count(), "The total count should be 1");
        assertEquals(1, result.total_pages(), "The total pages should be 1");
        assertEquals(10, result.limit(), "The limit should be 10");
        assertEquals(1, result.current_page(), "The current page should be 1");
        assertNull(result.previous_page(), "The previous page should be null");
        assertNull(result.next_page(), "The next page should be null");
        assertEquals(bookResponses, result.page());

        verify(bookService, times(1)).findAll(eq(titleFilter), eq(null), eq(null), any(Pageable.class));
    }

    @Test
    @DisplayName("Should return a book answer page when submitting an author filter")
    void findAll_WithAuthorFilter_ShouldReturnPageOfBookResponses() {
        String authorFilter = "Robert C. Martin";
        List<BookResponse> bookResponses = Arrays.asList(bookResponse);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<BookResponse> expectedPage = new PageImpl<>(bookResponses, pageable, bookResponses.size());

        when(bookService.findAll(eq(null), eq(authorFilter), eq(null), any(Pageable.class))).thenReturn(expectedPage);

        ResponseEntity<CustomPageableResponse<BookResponse>> response = bookController.findAll(0, 10, null, authorFilter, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CustomPageableResponse<BookResponse> result = response.getBody();
        assertNotNull(result);

        assertFalse(result.page().isEmpty(), "The result page should not be empty");
        assertEquals(1, result.page().size(), "The page should contain 1 book");
        assertEquals(1, result.total_count(), "The total count should be 1");
        assertEquals(1, result.total_pages(), "The total pages should be 1");
        assertEquals(10, result.limit(), "The limit should be 10");
        assertEquals(1, result.current_page(), "The current page should be 1");
        assertNull(result.previous_page(), "The previous page should be null");
        assertNull(result.next_page(), "The next page should be null");
        assertEquals(bookResponses, result.page());

        verify(bookService, times(1)).findAll(eq(null), eq(authorFilter), eq(null), any(Pageable.class));
    }

    @Test
    @DisplayName("Should return a page of book responses when multiple filters are submitted")
    void findAll_WithMultipleFilters_ShouldReturnPageOfBookResponses() {
        String titleFilter = "Clean Code";
        String authorFilter = "Robert C. Martin";
        List<BookResponse> bookResponses = Arrays.asList(bookResponse);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<BookResponse> expectedPage = new PageImpl<>(bookResponses, pageable, bookResponses.size());

        when(bookService.findAll(eq(titleFilter), eq(authorFilter), eq(null), any(Pageable.class))).thenReturn(expectedPage);

        ResponseEntity<CustomPageableResponse<BookResponse>> response = bookController.findAll(0, 10, titleFilter, authorFilter, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CustomPageableResponse<BookResponse> result = response.getBody();
        assertNotNull(result);

        assertFalse(result.page().isEmpty(), "The result page should not be empty");
        assertEquals(1, result.page().size(), "The page should contain 1 book");
        assertEquals(1, result.total_count(), "The total count should be 1");
        assertEquals(1, result.total_pages(), "The total pages should be 1");
        assertEquals(10, result.limit(), "The limit should be 10");
        assertEquals(1, result.current_page(), "The current page should be 1");
        assertNull(result.previous_page(), "The previous page should be null");
        assertNull(result.next_page(), "The next page should be null");
        assertEquals(bookResponses, result.page());

        verify(bookService, times(1)).findAll(eq(titleFilter), eq(authorFilter), eq(null), any(Pageable.class));
    }

    @Test
    @DisplayName("Should return an empty page when filters do not find books")
    void findAll_WithFilters_WhenNoBooksFound_ShouldReturnEmptyPage() {
        String titleFilter = "NonExistentTitle";
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<BookResponse> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(bookService.findAll(eq(titleFilter), eq(null), eq(null), any(Pageable.class))).thenReturn(emptyPage);

        ResponseEntity<CustomPageableResponse<BookResponse>> response = bookController.findAll(0, 10, titleFilter, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CustomPageableResponse<BookResponse> result = response.getBody();
        assertNotNull(result);

        assertTrue(result.page().isEmpty(), "The result page should be empty");
        assertEquals(0, result.page().size(), "The page should contain 0 books");
        assertEquals(0, result.total_count(), "The total count should be 0");
        assertEquals(0, result.total_pages(), "The total pages should be 0");
        assertEquals(10, result.limit(), "The limit should be 10");
        assertEquals(1, result.current_page(), "The current page should be 1");
        assertNull(result.previous_page(), "The previous page should be null");
        assertNull(result.next_page(), "The next page should be null");

        verify(bookService, times(1)).findAll(eq(titleFilter), eq(null), eq(null), any(Pageable.class));
    }

    @Test
    @DisplayName("Should return a page of book responses when empty filters are submitted and books exist")
    void findAll_WithEmptyStringFilters_ShouldReturnPageOfBookResponses() {
        String emptyTitle = "";
        String emptyAuthor = "";
        String emptyGender = "";
        List<BookResponse> bookResponses = Arrays.asList(bookResponse);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<BookResponse> expectedPage = new PageImpl<>(bookResponses, pageable, bookResponses.size());

        when(bookService.findAll(eq(emptyTitle), eq(emptyAuthor), eq(emptyGender), any(Pageable.class))).thenReturn(expectedPage);

        ResponseEntity<CustomPageableResponse<BookResponse>> response = bookController.findAll(0, 10, emptyTitle, emptyAuthor, emptyGender);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CustomPageableResponse<BookResponse> result = response.getBody();
        assertNotNull(result);

        assertFalse(result.page().isEmpty(), "The result page should not be empty");
        assertEquals(1, result.page().size(), "The page should contain 1 book");
        assertEquals(1, result.total_count(), "The total count should be 1");
        assertEquals(1, result.total_pages(), "The total pages should be 1");
        assertEquals(10, result.limit(), "The limit should be 10");
        assertEquals(1, result.current_page(), "The current page should be 1");
        assertNull(result.previous_page(), "The previous page should be null");
        assertNull(result.next_page(), "The next page should be null");
        assertEquals(bookResponses, result.page());

        verify(bookService, times(1)).findAll(eq(emptyTitle), eq(emptyAuthor), eq(emptyGender), any(Pageable.class));
    }

    @Test
    @DisplayName("Should find a book by ID")
    void findById_ShouldReturnBook_WhenBookExists() {
        when(bookService.findById(bookId)).thenReturn(bookResponse);

        ResponseEntity<BookResponse> response = bookController.findById(bookId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertBookEquals(bookResponse, response.getBody());
        verify(bookService, times(1)).findById(bookId);
    }

    @Test
    void getBookByIsbn_BookNotFound_ReturnsNotFound() {
        when(openLibraryService.findBookByIsbnWithExternalSearch(ISBN)).thenReturn(null);

        ResponseEntity<BookResponseDTO> response = bookController.getBookByIsbn(ISBN);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(openLibraryService, times(1)).findBookByIsbnWithExternalSearch(ISBN);
        verify(bookRepository, never()).existsByIsbn(anyString());
    }

    @Test
    @DisplayName("Should create a new book successfully")
    void createBook_ShouldReturnCreatedBook() {
        when(bookService.save(any(BookRequest.class))).thenReturn(bookResponse);

        ResponseEntity<BookResponse> response = bookController.create(bookRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertBookEquals(bookResponse, response.getBody());
        verify(bookService, times(1)).save(any(BookRequest.class));
    }

    private void assertBookEquals(BookResponse expected, BookResponse actual) {
        assertAll("Check book properties",
                () -> assertEquals(expected.id(), actual.id(), "The ID does not match"),
                () -> assertEquals(expected.gender(), actual.gender(), "Gender does not match"),
                () -> assertEquals(expected.author(), actual.author(), "The author does not match"),
                () -> assertEquals(expected.image(), actual.image(), "The image does not match"),
                () -> assertEquals(expected.title(), actual.title(), "The title does not match"),
                () -> assertEquals(expected.subtitle(), actual.subtitle(), "The subtitle does not match"),
                () -> assertEquals(expected.publisher(), actual.publisher(), "The publisher does not match"),
                () -> assertEquals(expected.year(), actual.year(), "The year does not match"),
                () -> assertEquals(expected.pages(), actual.pages(), "The page count does not match"),
                () -> assertEquals(expected.isbn(), actual.isbn(), "The ISBN does not match")
        );
    }

    @Test
    @DisplayName("Should update an existing book")
    void update_ShouldReturnUpdatedBook() {
        when(bookService.update(eq(bookId), any(BookRequest.class))).thenReturn(bookResponse);

        ResponseEntity<BookResponse> response = bookController.update(bookId, bookRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookResponse, response.getBody());
        verify(bookService, times(1)).update(eq(bookId), any(BookRequest.class));
    }

    @Test
    @DisplayName("Should delete a book successfully.")
    void delete_ShouldCallDeleteMethod() {
        bookController.delete(bookId);

        verify(bookService, times(1)).delete(bookId);
    }
}
