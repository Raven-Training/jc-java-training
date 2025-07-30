package com.raven.training.persistence.repository;

import com.raven.training.persistence.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IBookRepository extends JpaRepository<Book, UUID> {

    List<Book> findAllByIdIn(List<UUID> bookIds);

    Page<Book> findAll(Pageable pageable);
}
