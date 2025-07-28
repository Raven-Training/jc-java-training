package com.raven.training.persistence.repository;

import com.raven.training.persistence.entity.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IUserRepository extends ListCrudRepository<User, UUID> {
}
