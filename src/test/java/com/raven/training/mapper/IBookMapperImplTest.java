package com.raven.training.mapper;

import com.raven.training.persistence.entity.Book;
import com.raven.training.presentation.dto.book.BookRequest;
import com.raven.training.presentation.dto.book.BookResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for IBookMapperImplTest")
class IBookMapperImplTest {

    private IBookMapper bookMapper;
    private Book book;
    private BookRequest bookRequest;

    @BeforeEach
    void setUp() {
        bookMapper = Mappers.getMapper(IBookMapper.class);

        bookRequest = new BookRequest(
                "Ficción",
                "Gabriel García Márquez",
                "url-de-la-imagen.jpg",
                "Cien años de soledad",
                "Una obra maestra de la literatura latinoamericana",
                "Editorial Sudamericana",
                "1967",
                471,
                "978-0307474728"
        );

        book = Book.builder()
                .id(UUID.randomUUID())
                .gender("Ficción")
                .author("Gabriel García Márquez")
                .image("url-de-la-imagen.jpg")
                .title("Cien años de soledad")
                .subtitle("Una obra maestra de la literatura latinoamericana")
                .publisher("Editorial Sudamericana")
                .year("1967")
                .pages(471)
                .isbn("978-0307474728")
                .build();
    }

    @Test
    @DisplayName("Should map correctly from BookRequest to Book")
    void shouldMapBookRequestToBook() {
        Book result = bookMapper.toEntity(bookRequest);

        assertNotNull(result);
        assertEquals(bookRequest.gender(), result.getGender());
        assertEquals(bookRequest.author(), result.getAuthor());
        assertEquals(bookRequest.image(), result.getImage());
        assertEquals(bookRequest.title(), result.getTitle());
        assertEquals(bookRequest.subtitle(), result.getSubtitle());
        assertEquals(bookRequest.publisher(), result.getPublisher());
        assertEquals(bookRequest.year(), result.getYear());
        assertEquals(bookRequest.pages(), result.getPages());
        assertEquals(bookRequest.isbn(), result.getIsbn());
        assertNull(result.getId(), "El ID debe ser nulo ya que no se mapea desde BookRequest");
    }

    @Test
    @DisplayName("Should map correctly from Book to BookResponse")
    void shouldMapBookToBookResponse() {
        BookResponse result = bookMapper.toResponse(book);

        assertNotNull(result);
        assertEquals(book.getId(), result.id());
        assertEquals(book.getGender(), result.gender());
        assertEquals(book.getAuthor(), result.author());
        assertEquals(book.getImage(), result.image());
        assertEquals(book.getTitle(), result.title());
        assertEquals(book.getSubtitle(), result.subtitle());
        assertEquals(book.getPublisher(), result.publisher());
        assertEquals(book.getYear(), result.year());
        assertEquals(book.getPages(), result.pages());
        assertEquals(book.getIsbn(), result.isbn());
    }

    @Test
    @DisplayName("Should correctly map a Book list to BookResponse")
    void shouldMapBookListToBookResponseList() {
        List<Book> bookList = Arrays.asList(book);

        List<BookResponse> result = bookMapper.toResponseList(bookList);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(book.getId(), result.get(0).id());
        assertEquals(book.getTitle(), result.get(0).title());
        assertEquals(book.getAuthor(), result.get(0).author());
    }

    @Test
    @DisplayName("Should correctly handle null values in BookRequest")
    void shouldHandleNullValuesInBookRequest() {
        BookRequest nullRequest = new BookRequest(
                null, null, null, null, null, null, null, null, null
        );

        Book result = bookMapper.toEntity(nullRequest);

        assertNotNull(result);
        assertNull(result.getGender());
        assertNull(result.getAuthor());
        assertNull(result.getImage());
        assertNull(result.getTitle());
        assertNull(result.getSubtitle());
        assertNull(result.getPublisher());
        assertNull(result.getYear());
        assertNull(result.getPages());
        assertNull(result.getIsbn());
    }

    @Test
    @DisplayName("Should correctly handle a null Book")
    void shouldHandleNullBook() {
        BookResponse result = bookMapper.toResponse(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Should correctly handle a null list of books")
    void shouldHandleNullBookList() {
        List<BookResponse> result = bookMapper.toResponseList(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Should correctly handle an empty list of books")
    void shouldHandleEmptyBookList() {
        List<BookResponse> result = bookMapper.toResponseList(List.of());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
