package com.raven.training.persistence.repository;

import com.raven.training.persistence.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IBookRepository extends JpaRepository<Book, UUID> {

    @Query("""
        SELECT b FROM Book b 
        WHERE (COALESCE(:title, '') = '' OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) 
        AND (COALESCE(:author, '') = '' OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) 
        AND (COALESCE(:gender, '') = '' OR LOWER(b.gender) = LOWER(:gender))
    """)
    Page<Book> findAllWithFilters(
            @Param("title") String title,
            @Param("author") String author,
            @Param("gender") String gender,
            Pageable pageable
    );
}
