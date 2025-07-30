package com.raven.training.presentation.controller;

import com.raven.training.mapper.IBookMapper;
import com.raven.training.persistence.entity.Book;
import com.raven.training.persistence.repository.IBookRepository;
import com.raven.training.presentation.dto.book.BookRequest;
import com.raven.training.presentation.dto.book.BookResponse;
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

    @InjectMocks
    private BookController bookController;

    private BookRequest bookRequest;
    private BookResponse bookResponse;
    private final UUID bookId = UUID.randomUUID();

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
    @DisplayName("Should return a page of book responses when books exist")
    void findAll_WhenBooksExist_ShouldReturnPageOfBookResponses() {
        // Arrange
        List<BookResponse> bookResponses = Arrays.asList(bookResponse, bookResponse);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());
        Page<BookResponse> expectedPage = new PageImpl<>(bookResponses, pageable, bookResponses.size());

        when(bookService.findAll(any(Pageable.class))).thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<BookResponse>> response = bookController.findAll(0, 10);

        // Assert
        assertNotNull(response, "The answer should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "The status code should be 200");

        Page<BookResponse> result = response.getBody();
        assertNotNull(result, "The response body should not be null");
        assertEquals(2, result.getContent().size(), "Should return 2 books");
        assertEquals(2, result.getTotalElements(), "The total number of elements should be 2");
        assertTrue(result.isFirst(), "It should be the first page");

        verify(bookService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should return empty page when no books exist")
    void findAll_WhenNoBooksExist_ShouldReturnEmptyPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());
        Page<BookResponse> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(bookService.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // Act
        ResponseEntity<Page<BookResponse>> response = bookController.findAll(0, 10);

        // Assert
        assertNotNull(response, "The answer should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "The status code should be 200");

        Page<BookResponse> result = response.getBody();
        assertNotNull(result, "The response body should not be null");
        assertTrue(result.isEmpty(), "The result should be empty");
        assertEquals(0, result.getTotalElements(), "There should be no elements");

        verify(bookService, times(1)).findAll(any(Pageable.class));
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

    // Metodo auxiliar para verificar la igualdad de libros
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
