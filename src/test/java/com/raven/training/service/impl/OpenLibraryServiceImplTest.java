package com.raven.training.service.impl;

import com.raven.training.persistence.entity.Book;
import com.raven.training.persistence.repository.IBookRepository;
import com.raven.training.presentation.dto.book.bookexternal.BookResponseDTO;
import com.raven.training.presentation.dto.book.bookexternal.OpenLibraryBookDTO;
import com.raven.training.service.implementation.OpenLibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class OpenLibraryServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private IBookRepository bookRepository;

    @InjectMocks
    private OpenLibraryService openLibraryService;

    private static final String ISBN = "9780321765723";
    private static final String OPEN_LIBRARY_URL = "https://openlibrary.org/api/books?bibkeys=ISBN:" + ISBN + "&format=json&jscmd=data";

    private Book localBook;
    private OpenLibraryBookDTO externalBookDto;

    @BeforeEach
    void setUp() {
        localBook = Book.builder()
                .isbn(ISBN)
                .title("Effective Java")
                .author("Joshua Bloch")
                .publisher("Addison-Wesley Professional")
                .year("2017")
                .pages(416)
                .build();

        OpenLibraryBookDTO.BookData bookData = new OpenLibraryBookDTO.BookData();
        bookData.setTitle("Effective Java");
        bookData.setSubtitle("3rd Edition");
        bookData.setPublishDate("2017");
        bookData.setNumberOfPages(416);

        OpenLibraryBookDTO.Author author = new OpenLibraryBookDTO.Author();
        author.setName("Joshua Bloch");
        bookData.setAuthors(Collections.singletonList(author));

        OpenLibraryBookDTO.Publisher publisher = new OpenLibraryBookDTO.Publisher();
        publisher.setName("Addison-Wesley Professional");
        bookData.setPublishers(Collections.singletonList(publisher));

        externalBookDto = new OpenLibraryBookDTO();
        externalBookDto.setBooks(Collections.singletonMap("ISBN:" + ISBN, bookData));
    }

    @Test
    @DisplayName("Should return a book DTO when the external API call is successful")
    void getBookInfo_Success() {
        when(restTemplate.getForEntity(eq(OPEN_LIBRARY_URL), eq(OpenLibraryBookDTO.class)))
                .thenReturn(new ResponseEntity<>(externalBookDto, HttpStatus.OK));

        BookResponseDTO result = openLibraryService.getBookInfo(ISBN);

        assertNotNull(result);
        assertEquals("Effective Java", result.getTitle());
        assertEquals("Joshua Bloch", result.getAuthors().get(0));
        verify(restTemplate, times(1)).getForEntity(eq(OPEN_LIBRARY_URL), eq(OpenLibraryBookDTO.class));
    }

    @Test
    @DisplayName("Should return null when the external API returns an error status")
    void getBookInfo_ApiReturnsError_ReturnsNull() {
        when(restTemplate.getForEntity(eq(OPEN_LIBRARY_URL), eq(OpenLibraryBookDTO.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        BookResponseDTO result = openLibraryService.getBookInfo(ISBN);

        assertNull(result);
        verify(restTemplate, times(1)).getForEntity(eq(OPEN_LIBRARY_URL), eq(OpenLibraryBookDTO.class));
    }

    @Test
    @DisplayName("Should return null when the external API call throws an exception")
    void getBookInfo_ApiThrowsException_ReturnsNull() {
        when(restTemplate.getForEntity(eq(OPEN_LIBRARY_URL), eq(OpenLibraryBookDTO.class)))
                .thenThrow(new RuntimeException("API is down"));

        BookResponseDTO result = openLibraryService.getBookInfo(ISBN);

        assertNull(result);
        verify(restTemplate, times(1)).getForEntity(eq(OPEN_LIBRARY_URL), eq(OpenLibraryBookDTO.class));
    }


    @Test
    @DisplayName("Should return a book DTO when the book is found in the local database")
    void findBookByIsbn_FoundLocally_ReturnsBookDTO() {
        when(bookRepository.findByIsbn(ISBN)).thenReturn(Optional.of(localBook));

        BookResponseDTO result = openLibraryService.findBookByIsbn(ISBN);

        assertNotNull(result);
        assertEquals(localBook.getTitle(), result.getTitle());
        verify(bookRepository, times(1)).findByIsbn(ISBN);
    }

    @Test
    @DisplayName("Should return null when the book is not found in the local database")
    void findBookByIsbn_NotFoundLocally_ReturnsNull() {
        when(bookRepository.findByIsbn(ISBN)).thenReturn(Optional.empty());

        BookResponseDTO result = openLibraryService.findBookByIsbn(ISBN);

        assertNull(result);
        verify(bookRepository, times(1)).findByIsbn(ISBN);
    }

    @Test
    @DisplayName("Should return a book DTO and not call the external API when found locally")
    void findBookByIsbnWithExternalSearch_FoundLocally_ReturnsBookDTO() {
        OpenLibraryService spyService = spy(openLibraryService);
        BookResponseDTO localBookDto = new BookResponseDTO("9780321765723", "Effective Java", "3rd Edition", Collections.singletonList("Addison-Wesley Professional"), "2017", 416, Collections.singletonList("Joshua Bloch"));
        doReturn(localBookDto).when(spyService).findBookByIsbn(ISBN);

        BookResponseDTO result = spyService.findBookByIsbnWithExternalSearch(ISBN);

        assertNotNull(result);
        assertEquals(localBookDto.getTitle(), result.getTitle());
        verify(spyService, never()).getBookInfo(anyString());
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should save the book and return its DTO when found only in the external API")
    void findBookByIsbnWithExternalSearch_FoundExternally_SavesAndReturnsBookDTO() {
        OpenLibraryService spyService = spy(openLibraryService);
        doReturn(null).when(spyService).findBookByIsbn(ISBN);

        BookResponseDTO externalBookDto = new BookResponseDTO("9780321765723", "Effective Java", "3rd Edition", Collections.singletonList("Addison-Wesley Professional"), "2017", 416, Collections.singletonList("Joshua Bloch"));
        doReturn(externalBookDto).when(spyService).getBookInfo(ISBN);

        BookResponseDTO result = spyService.findBookByIsbnWithExternalSearch(ISBN);

        assertNotNull(result);
        assertEquals(externalBookDto.getTitle(), result.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Should return null when the book is not found in the local database or external API")
    void findBookByIsbnWithExternalSearch_NotFoundAnywhere_ReturnsNull() {
        OpenLibraryService spyService = spy(openLibraryService);
        doReturn(null).when(spyService).findBookByIsbn(ISBN);
        doReturn(null).when(spyService).getBookInfo(ISBN);

        BookResponseDTO result = spyService.findBookByIsbnWithExternalSearch(ISBN);

        assertNull(result);
        verify(bookRepository, never()).save(any());
    }
}

