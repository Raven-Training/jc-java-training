package com.raven.training.models.repository;

import com.raven.training.models.entity.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IUserRepository extends ListCrudRepository<User, UUID> {

    /**
     * Busca un usuario por su nombre de usuario.
     * @param username El nombre de usuario a buscar
     * @return El usuario encontrado o null si no existe
     */
    User findUserByUserName(String username);
}
