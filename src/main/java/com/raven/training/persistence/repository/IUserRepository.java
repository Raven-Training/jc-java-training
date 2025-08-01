package com.raven.training.persistence.repository;

import com.raven.training.persistence.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {

    Page<User> findAll(Pageable pageable);

    Optional<User> findUserByUserName(String username);
}
