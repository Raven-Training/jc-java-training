package com.raven.training.presentation.controller;

import com.raven.training.presentation.dto.book.BookRequest;
import com.raven.training.presentation.dto.book.BookResponse;
import com.raven.training.service.interfaces.IBookService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller to manage book-related operations
 */
@RestController
@RequestMapping("/api/books")
@AllArgsConstructor
public class BookController {

    private IBookService bookService;

    @GetMapping("/findAll")
    public ResponseEntity<List<BookResponse>> findAll(){
        return new ResponseEntity<>(bookService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<BookResponse> findById(@PathVariable UUID id){
       return new ResponseEntity<>(bookService.findById(id), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<BookResponse> create(@RequestBody BookRequest bookRequest){
        return new ResponseEntity<>(bookService.save(bookRequest), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BookResponse> update(@PathVariable UUID id, @RequestBody BookRequest bookRequest){
        return new ResponseEntity<>(bookService.update(id, bookRequest), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id){
        bookService.delete(id);
    }
}








