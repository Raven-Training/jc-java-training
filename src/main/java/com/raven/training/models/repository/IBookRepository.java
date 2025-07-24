package com.raven.training.models.repository;

import com.raven.training.models.entity.Book;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IBookRepository extends ListCrudRepository<Book, UUID> {

    /**
     * Busca un libro por su autor.
     * @param author El nombre del autor a buscar
     * @return El autor encontrado o null si no existe
     */
    Optional<Book> findBookByAuthor(String author);
}
