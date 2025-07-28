package com.raven.training.persistence.repository;

import com.raven.training.persistence.entity.Book;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IBookRepository extends ListCrudRepository<Book, UUID> {

    /**
     * Busca un libro por su id.
     * @param id El id del libro a buscar
     * @return El libro encontrado o null si no existe
     */
    List<Book> findAllByIdIn(List<UUID> bookIds);
}
