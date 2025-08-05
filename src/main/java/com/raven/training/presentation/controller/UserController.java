package com.raven.training.presentation.controller;

import com.raven.training.presentation.dto.pagination.CustomPageableResponse;
import com.raven.training.presentation.dto.user.UserRequest;
import com.raven.training.presentation.dto.user.UserResponse;
import com.raven.training.service.interfaces.IUserService;
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
 * REST controller for managing user-related operations.
 * This class exposes endpoints for CRUD operations on users,
 * as well as for managing their book collections and retrieving
 * the currently authenticated user.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private IUserService userService;

    /**
     * Retrieves a paginated list of all users.
     *
     * @param page The page number to retrieve (default is 0).
     * @param size The number of items per page (default is 10).
     * @return A {@link ResponseEntity} containing a custom paginated
     * response of {@link UserResponse} and an HTTP status of 200 (OK).
     */
    @GetMapping("/findAll")
    public ResponseEntity<CustomPageableResponse<UserResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<UserResponse> usersPage = userService.findAll(pageable);

        CustomPageableResponse<UserResponse> response = new CustomPageableResponse<>(
                usersPage.getContent(),
                usersPage.getNumberOfElements(),
                usersPage.getSize(),
                usersPage.getNumber() * usersPage.getSize(),
                usersPage.getTotalPages(),
                usersPage.getTotalElements(),
                usersPage.hasPrevious() ? usersPage.getNumber() : null,
                usersPage.getNumber() + 1,
                usersPage.hasNext() ? usersPage.getNumber() + 2 : null
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id The UUID of the user to retrieve.
     * @return A {@link ResponseEntity} with the found {@link UserResponse}
     * and an HTTP status of 200 (OK).
     */
    @GetMapping("/findById/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }

    /**
     * Updates an existing user's data.
     *
     * @param id The UUID of the user to update.
     * @param userRequest The {@link UserRequest} object with the updated data.
     * @return A {@link ResponseEntity} with the updated {@link UserResponse}
     * and an HTTP status of 200 (OK).
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @RequestBody UserRequest userRequest) {
        return new ResponseEntity<>(userService.update(id, userRequest), HttpStatus.OK);
    }

    /**
     * Deletes a user by their unique identifier.
     *
     * @param id The UUID of the user to delete.
     */
    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.delete(id);
    }

    /**
     * Adds a book to a user's collection.
     *
     * @param userId The UUID of the user.
     * @param bookId The UUID of the book to add.
     * @return A {@link ResponseEntity} with the updated {@link UserResponse}
     * and an HTTP status of 200 (OK).
     */
    @PostMapping("/{userId}/books/{bookId}")
    public ResponseEntity<UserResponse> addBookToUser(@PathVariable UUID userId, @PathVariable UUID bookId) {
        return new ResponseEntity<>(userService.addBookToUser(userId, bookId), HttpStatus.OK);
    }

    /**
     * Removes a book from a user's collection.
     *
     * @param userId The UUID of the user.
     * @param bookId The UUID of the book to remove.
     * @return A {@link ResponseEntity} with the updated {@link UserResponse}
     * and an HTTP status of 200 (OK).
     */
    @DeleteMapping("/{userId}/books/{bookId}")
    public ResponseEntity<UserResponse> removeBookFromUser(@PathVariable UUID userId, @PathVariable UUID bookId) {
        return new ResponseEntity<>(userService.removeBookFromUser(userId, bookId), HttpStatus.OK);
    }

    /**
     * Retrieves the details of the currently authenticated user.
     *
     * @return A {@link ResponseEntity} with the current {@link UserResponse}
     * and an HTTP status of 200 (OK).
     */
    @GetMapping("/logged")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return new ResponseEntity<>(userService.getCurrentUser(), HttpStatus.OK);
    }
}
