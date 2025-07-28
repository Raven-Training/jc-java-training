package com.raven.training.service.interfaces;

import com.raven.training.presentation.dto.book.BookRequest;
import com.raven.training.presentation.dto.book.BookResponse;

import java.util.List;
import java.util.UUID;

public interface IBookService {

    List<BookResponse> findAll();
    BookResponse findById(UUID id);
    BookResponse save(BookRequest bookRequest);
    BookResponse update(UUID id, BookRequest bookRequest);
    void delete(UUID id);


}
