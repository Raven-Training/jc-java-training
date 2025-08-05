package com.raven.training.persistence.repository;

import com.raven.training.persistence.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing persistence operations for the {@link User} entity.
 * This interface extends {@link JpaRepository}, providing standard CRUD functionality
 * and additional custom query methods for user-related data.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {

    /**
     * Retrieves a paginated list of all users.
     *
     * @param pageable Pagination and sorting information.
     * @return A {@link Page} of {@link User} objects.
     */
    Page<User> findAll(Pageable pageable);

    /**
     * Finds a user by their username.
     *
     * @param username The username of the user to find.
     * @return An {@link Optional} containing the {@link User} if found,
     * or an empty Optional otherwise.
     */
    Optional<User> findUserByUserName(String username);
}
