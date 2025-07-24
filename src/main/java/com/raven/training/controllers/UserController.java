package com.raven.training.controllers;

import com.raven.training.exception.UserNotFoundException;
import com.raven.training.exception.UserValidationException;
import com.raven.training.models.entity.User;
import com.raven.training.models.entity.Book;
import com.raven.training.models.repository.IUserRepository;
import com.raven.training.exception.BookAlreadyOwnedException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private IUserRepository userRepository;

    //Get /api/users/findAll - Obtener todos los usuarios
    @GetMapping("/findAll")
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    //Get /api/users/findById/{id} - Obtener un usuario por ID
    @GetMapping("/findById/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User whit " + id + " not found"));

        return ResponseEntity.ok(user);
    }

    //Post /api/users/create - Crear un nuevo usuario
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (user.getId() == null) {
            throw new UserValidationException("ID should not be provided when creating a new user");
        }
        User userSaved = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(userSaved);
    }

    //Put /api/users/update/{id} - Actualizar un usuario existente
    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @RequestBody User userDetails) {
        return userRepository.findById(id)
                .map((savedUser) -> {
                    savedUser.setUserName(userDetails.getUserName());
                    savedUser.setName(userDetails.getName());
                    savedUser.setBirthDate(userDetails.getBirthDate());

                    return ResponseEntity.ok(userRepository.save(savedUser));
                })
                .orElseThrow(() -> new UserNotFoundException("User whit " + id + " not found"));
    }

    //Delete /api/users/delete/{id} - Eliminar un usuario
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User whit " + id + " not found"));

        userRepository.delete(user);
        return ResponseEntity.ok().build();
    }

    //Post /api/users/{userId}/books - Añadir un libro a la colección del usuario
    @PostMapping("/{userId}/books")
    public ResponseEntity<User> addBookToCollection(@PathVariable UUID userId, @RequestBody Book book) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User whit " + userId + " not found"));
        
        try {
            user.addBook(book);
        } catch (BookAlreadyOwnedException e) {
            throw new RuntimeException("The user already has this book in their collection");
        }
        
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    //Delete /api/users/{userId}/books/{bookId} - Eliminar un libro de la colección del usuario
    @DeleteMapping("/{userId}/books/{bookId}")
    public ResponseEntity<User> removeBookFromCollection(@PathVariable UUID userId, @PathVariable UUID bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User whit " + userId + " not found"));

        user.removeBook(bookId);
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }
}
