package com.raven.training.service.interfaces;

import com.raven.training.presentation.dto.book.BookRequest;
import com.raven.training.presentation.dto.book.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Interface for the Book service.
 * Defines the contract for managing book-related business logic, including
 * CRUD operations and advanced search functionality.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
public interface IBookService {

    /**
     * Retrieves a paginated list of books.
     * The results can be filtered by title, author, and gender.
     *
     * @param title The title of the book to filter by (optional).
     * @param author The author of the book to filter by (optional).
     * @param gender The gender of the book to filter by (optional).
     * @param pageable Pagination and sorting information.
     * @return A {@link Page} of {@link BookResponse} objects.
     */
    Page<BookResponse> findAll(String title, String author, String gender, Pageable pageable);

    /**
     * Finds a book by its unique identifier.
     *
     * @param id The UUID of the book to find.
     * @return The found {@link BookResponse} object.
     */
    BookResponse findById(UUID id);

    /**
     * Saves a new book to the repository.
     *
     * @param bookRequest The {@link BookRequest} object containing the new book's data.
     * @return The newly created {@link BookResponse} object.
     */
    BookResponse save(BookRequest bookRequest);

    /**
     * Updates an existing book with new data.
     *
     * @param id The UUID of the book to update.
     * @param bookRequest The {@link BookRequest} object with the updated data.
     * @return The updated {@link BookResponse} object.
     */
    BookResponse update(UUID id, BookRequest bookRequest);

    /**
     * Deletes a book from the repository by its ID.
     *
     * @param id The UUID of the book to delete.
     */
    void delete(UUID id);


}
