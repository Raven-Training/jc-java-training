package com.raven.training.presentation.controller;

import com.raven.training.presentation.dto.user.UserRequest;
import com.raven.training.presentation.dto.user.UserResponse;
import com.raven.training.service.interfaces.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador para gestionar las operaciones relacionadas con usuarios.
 */
@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private IUserService userService;

    //Get /api/users/findAll - Obtener todos los usuarios
    @GetMapping("/findAll")
    public ResponseEntity<List<UserResponse>> findAll() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    //Get /api/users/findById/{id} - Obtener un usuario por ID
    @GetMapping("/findById/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }

    //Post /api/users/create - Crear un nuevo usuario
    @PostMapping("/create")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        return new ResponseEntity<>(userService.save(userRequest), HttpStatus.CREATED);
    }

    //Put /api/users/update/{id} - Actualizar un usuario existente
    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @RequestBody UserRequest userRequest) {
        return new ResponseEntity<>(userService.update(id, userRequest), HttpStatus.OK);
    }

    //Delete /api/users/delete/{id} - Eliminar un usuario
    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.delete(id);
    }

    //Post /api/users/{userId}/books/{bookId}
    @PostMapping("/{userId}/books/{bookId}")
    public ResponseEntity<UserResponse> addBookToUser(@PathVariable UUID userId, @PathVariable UUID bookId) {
        return new ResponseEntity<>(userService.addBookToUser(userId, bookId), HttpStatus.OK);
    }

    //Post /api/users/{userId}/books/{bookId}
    @DeleteMapping("/{userId}/books/{bookId}")
    public ResponseEntity<UserResponse> removeBookFromUser(@PathVariable UUID userId, @PathVariable UUID bookId) {
        return new ResponseEntity<>(userService.removeBookFromUser(userId, bookId), HttpStatus.OK);
    }
}
