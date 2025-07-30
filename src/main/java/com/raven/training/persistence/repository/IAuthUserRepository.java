package com.raven.training.persistence.repository;

import com.raven.training.persistence.entity.AuthUser;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IAuthUserRepository extends ListCrudRepository<AuthUser, UUID> {

    Optional<AuthUser> findAuthUserByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
