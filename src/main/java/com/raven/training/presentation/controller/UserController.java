package com.raven.training.presentation.controller;

import com.raven.training.presentation.dto.user.UserRequest;
import com.raven.training.presentation.dto.user.UserResponse;
import com.raven.training.service.interfaces.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller to manage user-related operations
 */
@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private IUserService userService;

    @GetMapping("/findAll")
    public ResponseEntity<Page<UserResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return new ResponseEntity<>(userService.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        return new ResponseEntity<>(userService.save(userRequest), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @RequestBody UserRequest userRequest) {
        return new ResponseEntity<>(userService.update(id, userRequest), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.delete(id);
    }

    @PostMapping("/{userId}/books/{bookId}")
    public ResponseEntity<UserResponse> addBookToUser(@PathVariable UUID userId, @PathVariable UUID bookId) {
        return new ResponseEntity<>(userService.addBookToUser(userId, bookId), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/books/{bookId}")
    public ResponseEntity<UserResponse> removeBookFromUser(@PathVariable UUID userId, @PathVariable UUID bookId) {
        return new ResponseEntity<>(userService.removeBookFromUser(userId, bookId), HttpStatus.OK);
    }
}
