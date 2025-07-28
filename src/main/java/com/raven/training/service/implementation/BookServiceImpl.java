package com.raven.training.service.implementation;

import com.raven.training.exception.error.BookNotFoundException;
import com.raven.training.mapper.IBookMapper;
import com.raven.training.persistence.entity.Book;
import com.raven.training.persistence.repository.IBookRepository;
import com.raven.training.presentation.dto.book.BookRequest;
import com.raven.training.presentation.dto.book.BookResponse;
import com.raven.training.service.interfaces.IBookService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BookServiceImpl implements IBookService {

    private IBookRepository bookRepository;
    private IBookMapper bookMapper;

    @Override
    @Transactional
    public List<BookResponse> findAll() {
        List<Book> books = bookRepository.findAll();

        return bookMapper.toResponseList(books);
    }

    @Override
    @Transactional
    public BookResponse findById(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(BookNotFoundException::new);

        return bookMapper.toResponse(book);
    }

    @Override
    @Transactional
    public BookResponse save(BookRequest bookRequest) {
        Book book = bookMapper.toEntity(bookRequest);
        Book savedBook = bookRepository.save(book);

        return bookMapper.toResponse(savedBook);
    }

    @Override
    @Transactional
    public BookResponse update(UUID id, BookRequest bookRequest) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    if (bookRequest.gender() != null && !bookRequest.gender().trim().isEmpty()) {
                        existingBook.setGender(bookRequest.gender());
                    }
                    if (bookRequest.author() != null && !bookRequest.author().trim().isEmpty()){
                        existingBook.setAuthor(bookRequest.author());
                    }
                    if (bookRequest.image() != null && !bookRequest.image().trim().isEmpty()){
                        existingBook.setImage(bookRequest.image());
                    }
                    if (bookRequest.title() != null && !bookRequest.title().trim().isEmpty()){
                        existingBook.setTitle(bookRequest.title());
                    }
                    if (bookRequest.subtitle() != null && !bookRequest.subtitle().trim().isEmpty()){
                        existingBook.setSubtitle(bookRequest.subtitle());
                    }
                    if (bookRequest.publisher() != null && !bookRequest.publisher().trim().isEmpty()){
                        existingBook.setPublisher(bookRequest.publisher());
                    }
                    if (bookRequest.year() != null && !bookRequest.year().trim().isEmpty()){
                        existingBook.setYear(bookRequest.year());
                    }
                    if (bookRequest.pages() != null){
                        existingBook.setPages(bookRequest.pages());
                    }
                    if (bookRequest.isbn() != null && !bookRequest.isbn().trim().isEmpty()){
                        existingBook.setIsbn(bookRequest.isbn());
                    }

                     Book bookUpdated = bookRepository.save(existingBook);

                    return bookMapper.toResponse(bookUpdated);
                })
                .orElseThrow(BookNotFoundException::new);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(BookNotFoundException::new);

        bookRepository.delete(book);
    }
}





