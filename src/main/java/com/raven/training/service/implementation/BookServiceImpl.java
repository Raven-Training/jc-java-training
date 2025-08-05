package com.raven.training.service.implementation;

import com.raven.training.exception.error.BookNotFoundException;
import com.raven.training.mapper.IBookMapper;
import com.raven.training.persistence.entity.Book;
import com.raven.training.persistence.repository.IBookRepository;
import com.raven.training.presentation.dto.book.BookRequest;
import com.raven.training.presentation.dto.book.BookResponse;
import com.raven.training.service.interfaces.IBookService;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service implementation for managing book-related business logic.
 * This class handles all operations for the Book entity, including
 * retrieval, creation, modification, and deletion.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@Service
@AllArgsConstructor
public class BookServiceImpl implements IBookService {

    private IBookRepository bookRepository;
    private IBookMapper bookMapper;


    /**
     * Retrieves a paginated list of books, with optional filters for title, author, and gender.
     * If no filters are provided, it returns all books in a paginated format.
     *
     * @param title The title of the book to filter by (optional).
     * @param author The author of the book to filter by (optional).
     * @param gender The gender of the book to filter by (optional).
     * @param pageable Pagination and sorting information.
     * @return A {@link Page} of {@link BookResponse} objects.
     */
    @Override
    public Page<BookResponse> findAll(String title, String author, String gender, Pageable pageable) {

        if ((title == null || title.isEmpty()) &&
                (author == null || author.isEmpty()) &&
                (gender == null || gender.isEmpty())) {
            return bookRepository.findAll(pageable).map(bookMapper::toResponse);
        }

        return bookRepository.findAllWithFilters(
                title != null ? title : "",
                author != null ? author : "",
                gender != null ? gender : "",
                pageable
        ).map(bookMapper::toResponse);
    }

    /**
     * Finds a book by its unique identifier.
     *
     * @param id The UUID of the book to find.
     * @return The found {@link BookResponse} object.
     * @throws BookNotFoundException if a book with the given ID is not found.
     */
    @Override
    @Transactional(readOnly = true)
    public BookResponse findById(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(BookNotFoundException::new);

        return bookMapper.toResponse(book);
    }

    /**
     * Saves a new book based on the provided request data.
     *
     * @param bookRequest The {@link BookRequest} object containing the new book's data.
     * @return The newly created {@link BookResponse} object.
     */
    @Override
    @Transactional
    public BookResponse save(BookRequest bookRequest) {
        Book book = bookMapper.toEntity(bookRequest);
        Book savedBook = bookRepository.save(book);

        return bookMapper.toResponse(savedBook);
    }

    /**
     * Updates an existing book with new data.
     * It only updates the fields that are not null or empty in the request.
     *
     * @param id The UUID of the book to update.
     * @param bookRequest The {@link BookRequest} object with the updated data.
     * @return The updated {@link BookResponse} object.
     * @throws BookNotFoundException if a book with the given ID is not found.
     */
    @Override
    @Transactional
    public BookResponse update(UUID id, BookRequest bookRequest) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    if (bookRequest.gender() != null && !bookRequest.gender().trim().isEmpty()) {
                        existingBook.setGender(bookRequest.gender());
                    }
                    if (bookRequest.author() != null && !bookRequest.author().trim().isEmpty()){
                        existingBook.setAuthor(bookRequest.author());
                    }
                    if (bookRequest.image() != null && !bookRequest.image().trim().isEmpty()){
                        existingBook.setImage(bookRequest.image());
                    }
                    if (bookRequest.title() != null && !bookRequest.title().trim().isEmpty()){
                        existingBook.setTitle(bookRequest.title());
                    }
                    if (bookRequest.subtitle() != null && !bookRequest.subtitle().trim().isEmpty()){
                        existingBook.setSubtitle(bookRequest.subtitle());
                    }
                    if (bookRequest.publisher() != null && !bookRequest.publisher().trim().isEmpty()){
                        existingBook.setPublisher(bookRequest.publisher());
                    }
                    if (bookRequest.year() != null && !bookRequest.year().trim().isEmpty()){
                        existingBook.setYear(bookRequest.year());
                    }
                    if (bookRequest.pages() != null){
                        existingBook.setPages(bookRequest.pages());
                    }
                    if (bookRequest.isbn() != null && !bookRequest.isbn().trim().isEmpty()){
                        existingBook.setIsbn(bookRequest.isbn());
                    }

                     Book bookUpdated = bookRepository.save(existingBook);

                    return bookMapper.toResponse(bookUpdated);
                })
                .orElseThrow(BookNotFoundException::new);
    }

    /**
     * Deletes a book from the repository by its ID.
     *
     * @param id The UUID of the book to delete.
     * @throws BookNotFoundException if a book with the given ID is not found.
     */
    @Override
    @Transactional
    public void delete(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(BookNotFoundException::new);

        bookRepository.delete(book);
    }
}





