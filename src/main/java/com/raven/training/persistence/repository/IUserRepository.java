package com.raven.training.persistence.repository;

import com.raven.training.persistence.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {
    List<User> findAllByIdIn(Collection<UUID> ids);

    Page<User> findAll(Pageable pageable);

    Optional<User> findUserByUserName(String username);
}
