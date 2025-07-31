package com.raven.training.service.interfaces;

import com.raven.training.presentation.dto.book.BookRequest;
import com.raven.training.presentation.dto.book.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IBookService {

    Page<BookResponse> findAll(String title, String author, String gender, Pageable pageable);
    BookResponse findById(UUID id);
    BookResponse save(BookRequest bookRequest);
    BookResponse update(UUID id, BookRequest bookRequest);
    void delete(UUID id);


}
