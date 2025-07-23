package com.raven.training.models.repository;

import com.raven.training.models.entity.Book;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IBookRepository extends ListCrudRepository<Book, UUID> {

    Optional<Book> findBookByAuthor(String author);
}
