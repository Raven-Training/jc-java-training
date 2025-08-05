package com.raven.training.presentation.controller;

import com.raven.training.persistence.repository.IBookRepository;
import com.raven.training.presentation.dto.book.BookRequest;
import com.raven.training.presentation.dto.book.BookResponse;
import com.raven.training.presentation.dto.pagination.CustomPageableResponse;
import com.raven.training.service.implementation.OpenLibraryService;
import com.raven.training.presentation.dto.book.bookexternal.BookResponseDTO;
import com.raven.training.service.interfaces.IBookService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing book-related operations.
 * This class exposes endpoints for CRUD operations on books,
 * as well as searching for books by ISBN and handling paginated requests.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@RestController
@RequestMapping("/api/v1/books")
@AllArgsConstructor
public class BookController {

    private final IBookService bookService;
    private final OpenLibraryService openLibraryService;
    private final IBookRepository bookRepository;

    /**
     * Retrieves a paginated list of books with optional filtering.
     * The results can be filtered by title, author, and gender.
     *
     * @param page The page number to retrieve (default is 0).
     * @param size The number of items per page (default is 10).
     * @param title An optional title to filter the books.
     * @param author An optional author to filter the books.
     * @param gender An optional gender to filter the books.
     * @return A {@link ResponseEntity} containing a custom paginated
     * response of {@link BookResponse} and an HTTP status of 200 (OK).
     */
    @GetMapping("/findAll")
    public ResponseEntity<CustomPageableResponse<BookResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String gender) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<BookResponse> booksPage = bookService.findAll(title, author, gender, pageable);

        CustomPageableResponse<BookResponse> response = new CustomPageableResponse<>(
          booksPage.getContent(),
          booksPage.getNumberOfElements(),
          booksPage.getSize(),
          booksPage.getNumber() * booksPage.getSize(),
          booksPage.getTotalPages(),
          booksPage.getTotalElements(),
          booksPage.hasPrevious() ? booksPage.getNumber() : null,
          booksPage.getNumber() + 1,
          booksPage.hasNext() ? booksPage.getNumber() + 2 : null
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves a book by its unique identifier.
     *
     * @param id The UUID of the book to retrieve.
     * @return A {@link ResponseEntity} with the found {@link BookResponse}
     * and an HTTP status of 200 (OK).
     */
    @GetMapping("/findById/{id}")
    public ResponseEntity<BookResponse> findById(@PathVariable UUID id) {
        return new ResponseEntity<>(bookService.findById(id), HttpStatus.OK);
    }

    /**
     * Searches for a book by its ISBN.
     * It first checks the local database and, if not found, consults an external API.
     * If the book is found externally, it is created in the local database.
     *
     * @param isbn The ISBN of the book to search for.
     * @return A {@link ResponseEntity} with the book details.
     * Returns HTTP status 200 (OK) if found in the database, 201 (Created)
     * if found in the external API, or 404 (Not Found) if not found at all.
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookResponseDTO> getBookByIsbn(@PathVariable String isbn) {
        BookResponseDTO book = openLibraryService.findBookByIsbnWithExternalSearch(isbn);

        if (book != null) {
            boolean bookExists = bookRepository.existsByIsbn(isbn);
            return new ResponseEntity<>(
                    book,
                    bookExists ? HttpStatus.OK : HttpStatus.CREATED
            );
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * Creates a new book.
     *
     * @param bookRequest The {@link BookRequest} object containing the new book's data.
     * @return A {@link ResponseEntity} with the newly created {@link BookResponse}
     * and an HTTP status of 201 (Created).
     */
    @PostMapping("/create")
    public ResponseEntity<BookResponse> create(@RequestBody BookRequest bookRequest){
        return new ResponseEntity<>(bookService.save(bookRequest), HttpStatus.CREATED);
    }

    /**
     * Updates an existing book.
     *
     * @param id The UUID of the book to update.
     * @param bookRequest The {@link BookRequest} object with the updated data.
     * @return A {@link ResponseEntity} with the updated {@link BookResponse}
     * and an HTTP status of 200 (OK).
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<BookResponse> update(@PathVariable UUID id, @RequestBody BookRequest bookRequest){
        return new ResponseEntity<>(bookService.update(id, bookRequest), HttpStatus.OK);
    }

    /**
     * Deletes a book by its unique identifier.
     *
     * @param id The UUID of the book to delete.
     */
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id){
        bookService.delete(id);
    }
}








