package com.raven.training.persistence.repository;

import com.raven.training.persistence.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing persistence operations for the {@link Book} entity.
 * This interface extends {@link JpaRepository}, providing standard CRUD functionality
 * and additional custom query methods for filtering and searching books.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@Repository
public interface IBookRepository extends JpaRepository<Book, UUID> {

    /**
     * Finds a paginated list of books with optional filters.
     * The search is case-insensitive for all fields.
     *
     * @param title An optional title to filter books by (partial match).
     * @param author An optional author's name to filter books by (partial match).
     * @param gender An optional genre to filter books by (exact match).
     * @param pageable Pagination and sorting information.
     * @return A {@link Page} of {@link Book} objects that match the criteria.
     */
    @Query("""
        SELECT b FROM Book b 
        WHERE (COALESCE(:title, '') = '' OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) 
        AND (COALESCE(:author, '') = '' OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) 
        AND (COALESCE(:gender, '') = '' OR LOWER(b.gender) = LOWER(:gender))
    """)
    Page<Book> findAllWithFilters(
            @Param("title") String title,
            @Param("author") String author,
            @Param("gender") String gender,
            Pageable pageable
    );

    /**
     * Finds a book by its International Standard Book Number (ISBN).
     *
     * @param isbn The ISBN of the book to search for.
     * @return An {@link Optional} containing the {@link Book} if found,
     * or an empty Optional otherwise.
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Checks if a book with the specified ISBN exists in the repository.
     *
     * @param isbn The ISBN to check for.
     * @return {@code true} if a book with the ISBN exists, {@code false} otherwise.
     */
    boolean existsByIsbn(String isbn);
}
