package com.raven.training.persistence.repository;

import com.raven.training.persistence.entity.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface IUserRepository extends ListCrudRepository<User, UUID> {
    List<User> findAllByIdIn(Collection<UUID> ids);
}
