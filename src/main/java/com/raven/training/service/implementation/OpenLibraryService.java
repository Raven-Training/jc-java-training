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
 * Servicio para interactuar con la API de OpenLibrary
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenLibraryService {

    private static final String OPEN_LIBRARY_URL = "https://openlibrary.org/api/books";
    
    private final RestTemplate restTemplate;
    private final IBookRepository bookRepository;

    /**
     * Obtiene información de un libro por su ISBN desde la API de OpenLibrary
     * @param isbn ISBN del libro a buscar
     * @return DTO con la información del libro o null si no se encuentra
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
     * Busca un libro por ISBN, primero en la base de datos local
     * @param isbn ISBN del libro a buscar
     * @return DTO con la información del libro o null si no se encuentra
     */
    public BookResponseDTO findBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .map(this::mapToBookResponseDTO)
                .orElse(null);
    }
    
    /**
     * Busca un libro por ISBN, primero en la base de datos local y, si no se encuentra,
     * lo busca en la API externa y lo guarda en la base de datos
     * @param isbn ISBN del libro a buscar
     * @return DTO con la información del libro o null si no se encuentra
     */
    public BookResponseDTO findBookByIsbnWithExternalSearch(String isbn) {
        // Buscar primero en la base de datos local
        BookResponseDTO book = findBookByIsbn(isbn);
        if (book != null) {
            return book;
        }

        // Si no existe localmente, buscar en la API externa
        book = getBookInfo(isbn);
        if (book != null) {
            // Guardar en la base de datos
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

        // Get the first (and only) entry in the map
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
