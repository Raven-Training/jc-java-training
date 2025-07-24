package com.raven.training.controllers;

import com.raven.training.exception.BookNotFoundException;
import com.raven.training.exception.BookValidationException;
import com.raven.training.models.entity.Book;
import com.raven.training.models.repository.IBookRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@AllArgsConstructor
public class BookController {

    private IBookRepository bookRepository;

    //Get /api/books/find - Obtener todos los libros
    @GetMapping("/find")
    public ResponseEntity<List<Book>> findAll(){
        return ResponseEntity.ok(bookRepository.findAll());
    }

    //Get /api/books/findById/{id} - Obtener un libro por ID
    @GetMapping("/findById/{id}")
    public ResponseEntity<Book> findById(@PathVariable UUID id){
       Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Element whit " + id + " not found exist"));

       return ResponseEntity.ok(book);
    }

    //Post /api/books/create - Crear un nuevo libro
    @PostMapping("/create")
    public ResponseEntity<Book> create(@RequestBody Book book){
        if (book.getId() != null) {
            throw new BookValidationException("ID should not be provided when creating a new book");
        }
        Book bookSaved = bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookSaved);
    }

    //Put /api/books/update/{id} - Actualizar un libro
    @PutMapping("/update/{id}")
    public ResponseEntity<Book> update(@PathVariable UUID id, @RequestBody Book book){
        return bookRepository.findById(id)
                .map((savedBook) -> {
                    savedBook.setGender(book.getGender());
                    savedBook.setAuthor(book.getAuthor());
                    savedBook.setImage(book.getImage());
                    savedBook.setTitle(book.getTitle());
                    savedBook.setSubtitle(book.getSubtitle());
                    savedBook.setPublisher(book.getPublisher());
                    savedBook.setYear(book.getYear());
                    savedBook.setPages(book.getPages());
                    savedBook.setIsbn(book.getIsbn());
                    return ResponseEntity.ok(bookRepository.save(savedBook));
                })
                .orElseThrow(() -> new BookNotFoundException("Element whit " + id + " not found exist"));
    }

    //Delete /api/books/delete/{id} - Eliminar un libro
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Element whit " + id + " not found exist"));
        bookRepository.delete(book);
        return ResponseEntity.ok().build();
    }
}








