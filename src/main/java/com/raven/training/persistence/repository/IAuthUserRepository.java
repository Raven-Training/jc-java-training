package com.raven.training.persistence.repository;

import com.raven.training.persistence.entity.AuthUser;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing persistence operations for the {@link AuthUser} entity.
 * This interface extends {@link ListCrudRepository}, providing standard CRUD functionality
 * and additional custom query methods for authentication-related data.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@Repository
public interface IAuthUserRepository extends ListCrudRepository<AuthUser, UUID> {

    /**
     * Finds an authentication user by their username.
     *
     * @param username The username of the user to find.
     * @return An {@link Optional} containing the {@link AuthUser} if found,
     * or an empty Optional otherwise.
     */
    Optional<AuthUser> findAuthUserByUsername(String username);

    /**
     * Checks if a user with the given username already exists.
     *
     * @param username The username to check.
     * @return {@code true} if a user with the username exists, {@code false} otherwise.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user with the given email already exists.
     *
     * @param email The email address to check.
     * @return {@code true} if a user with the email exists, {@code false} otherwise.
     */
    boolean existsByEmail(String email);
}
