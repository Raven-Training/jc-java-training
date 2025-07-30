package com.raven.training.presentation.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    @DisplayName("Should return all books successfully")
    void findAll_ShouldReturnAllBooks() {

        List<BookResponse> expectedBooks = List.of(bookResponse);
        when(bookService.findAll()).thenReturn(expectedBooks);

        ResponseEntity<List<BookResponse>> response = bookController.findAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        BookResponse actualBook = response.getBody().get(0);
        assertBookEquals(bookResponse, actualBook);
        verify(bookService, times(1)).findAll();
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
