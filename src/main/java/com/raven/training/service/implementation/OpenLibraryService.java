package com.raven.training.service.implementation;

import com.raven.training.persistence.entity.Book;
import com.raven.training.persistence.repository.IBookRepository;
import com.raven.training.presentation.dto.book.bookexternal.BookResponseDTO;
import com.raven.training.presentation.dto.book.bookexternal.OpenLibraryBookDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service to interact with the OpenLibrary API to retrieve book information.
 * This class provides methods to search for books by their ISBN, first
 * in the local database and then, if not found, in the external API.
 * The retrieved data is then stored in the local database for future use.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenLibraryService {

    private static final String OPEN_LIBRARY_URL = "https://openlibrary.org/api/books";
    
    private final RestTemplate restTemplate;
    private final IBookRepository bookRepository;

    /**
     * Get information about a book by its ISBN from the OpenLibrary API
     * @param isbn ISBN of the book to search for
     * @return DTO with the book information or null if not found
     */
    public BookResponseDTO getBookInfo(String isbn) {
        String url = UriComponentsBuilder.fromUriString(OPEN_LIBRARY_URL)
                .queryParam("bibkeys", "ISBN:" + isbn)
                .queryParam("format", "json")
                .queryParam("jscmd", "data")
                .build()
                .toUriString();

        try {
            ResponseEntity<OpenLibraryBookDTO> response = restTemplate.getForEntity(
                    url, 
                    OpenLibraryBookDTO.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return mapToBookResponseDTO(isbn, response.getBody());
            }
        } catch (Exception e) {
            log.error("Error al consultar la API de OpenLibrary para el ISBN: " + isbn, e);
        }
        
        return null;
    }

    /**
     * Search for a book by ISBN, first in the local database
     * @param isbn Change access modifier
     * @return DTO with the book information or null if not found
     */
    public BookResponseDTO findBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .map(this::mapToBookResponseDTO)
                .orElse(null);
    }
    
    /**
     * Search for a book by ISBN, first in the local database and if not found,
     * it looks it up in the external API and saves it to the database
     * @param isbn ISBN of the book to search for
     * @return DTO with the book information or null if not found
     */
    public BookResponseDTO findBookByIsbnWithExternalSearch(String isbn) {
        BookResponseDTO book = findBookByIsbn(isbn);
        if (book != null) {
            return book;
        }

        book = getBookInfo(isbn);
        if (book != null) {
            Book newBook = mapToBookEntity(book);
            bookRepository.save(newBook);
            return book;
        }

        return null;
    }

    private BookResponseDTO mapToBookResponseDTO(Book book) {
        return BookResponseDTO.builder()
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .subtitle(book.getSubtitle())
                .publishers(book.getPublisher() != null ? 
                        Collections.singletonList(book.getPublisher()) :
                        Collections.emptyList())
                .publishDate(book.getYear())
                .numberOfPages(book.getPages())
                .authors(book.getAuthor() != null ? 
                        Collections.singletonList(book.getAuthor()) : 
                        Collections.emptyList())
                .build();
    }

    private BookResponseDTO mapToBookResponseDTO(String isbn, OpenLibraryBookDTO openLibraryBook) {
        if (openLibraryBook.getBooks() == null || openLibraryBook.getBooks().isEmpty()) {
            return null;
        }

        OpenLibraryBookDTO.BookData bookData = openLibraryBook.getBooks().values().iterator().next();
        
        if (bookData == null) {
            return null;
        }

        return BookResponseDTO.builder()
                .isbn(isbn)
                .title(bookData.getTitle())
                .subtitle(bookData.getSubtitle())
                .publishers(bookData.getPublishers() != null ?
                        bookData.getPublishers().stream()
                                .map(OpenLibraryBookDTO.Publisher::getName)
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .publishDate(bookData.getPublishDate())
                .numberOfPages(bookData.getNumberOfPages())
                .authors(bookData.getAuthors() != null ?
                        bookData.getAuthors().stream()
                                .map(OpenLibraryBookDTO.Author::getName)
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .build();
    }

    private Book mapToBookEntity(BookResponseDTO dto) {
        return Book.builder()
                .id(UUID.randomUUID())
                .isbn(dto.getIsbn())
                .title(dto.getTitle())
                .subtitle(dto.getSubtitle())
                .publisher(dto.getPublishers() != null && !dto.getPublishers().isEmpty() ? 
                        dto.getPublishers().get(0) : null)
                .year(dto.getPublishDate())
                .pages(dto.getNumberOfPages())
                .author(dto.getAuthors() != null && !dto.getAuthors().isEmpty() ? 
                        dto.getAuthors().get(0) : null)
                .build();
    }
}
