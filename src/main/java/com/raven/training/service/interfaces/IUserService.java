package com.raven.training.service.interfaces;

import com.raven.training.presentation.dto.user.UserRequest;
import com.raven.training.presentation.dto.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Interface for the User service.
 * Defines the contract for managing user-related business logic, including
 * CRUD operations, managing a user's book collection, and retrieving
 * the currently authenticated user.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
public interface IUserService {

    /**
     * Retrieves a paginated list of all users.
     *
     * @param pageable Pagination and sorting information.
     * @return A {@link Page} of {@link UserResponse} objects.
     */
    Page<UserResponse> findAll(Pageable pageable);

    /**
     * Finds a user by their unique identifier.
     *
     * @param id The UUID of the user to find.
     * @return The found {@link UserResponse} object.
     */
    UserResponse findById(UUID id);

    /**
     * Updates an existing user's data.
     *
     * @param id The UUID of the user to update.
     * @param userRequest The {@link UserRequest} object with the updated data.
     * @return The updated {@link UserResponse} object.
     */
    UserResponse update(UUID id, UserRequest userRequest);

    /**
     * Deletes a user from the repository by their ID.
     *
     * @param id The UUID of the user to delete.
     */
    void delete(UUID id);

    /**
     * Adds a book to a user's collection.
     *
     * @param userId The UUID of the user.
     * @param bookId The UUID of the book to add.
     * @return The updated {@link UserResponse} object.
     */
    UserResponse addBookToUser(UUID userId, UUID bookId);

    /**
     * Removes a book from a user's collection.
     *
     * @param userId The UUID of the user.
     * @param bookId The UUID of the book to remove.
     * @return The updated {@link UserResponse} object.
     */
    UserResponse removeBookFromUser(UUID userId, UUID bookId);

    /**
     * Retrieves the details of the currently authenticated user.
     *
     * @return The {@link UserResponse} object for the current user.
     */
    UserResponse getCurrentUser();
}
