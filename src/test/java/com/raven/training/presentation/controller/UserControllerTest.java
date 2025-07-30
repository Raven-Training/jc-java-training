package com.raven.training.presentation.controller;

import com.raven.training.presentation.dto.user.UserRequest;
import com.raven.training.presentation.dto.user.UserResponse;
import com.raven.training.service.interfaces.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for UserController")
class UserControllerTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private UserController userController;

    private UserRequest userRequest;
    private UserResponse userResponse;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest(
            "testuser",
            "Test User",
            LocalDate.of(1990, 1, 1),
            List.of()
        );

        userResponse = new UserResponse(
            userId,
            "testuser",
            "Test User",
            LocalDate.of(1990, 1, 1),
            List.of()
        );
    }

    @Test
    @DisplayName("Should return a page of users successfully")
    void findAll_ShouldReturnPageOfUsers() {
        // Arrange
        UserResponse userResponse2 = new UserResponse(
                UUID.randomUUID(),
                "anotheruser",
                "Another User",
                LocalDate.of(1995, 5, 15),
                List.of()
        );

        Page<UserResponse> expectedPage = new PageImpl<>(
                Arrays.asList(userResponse, userResponse2),
                PageRequest.of(0, 10, Sort.by("id").ascending()),
                2
        );

        when(userService.findAll(any(Pageable.class))).thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<UserResponse>> response = userController.findAll(0, 10);

        // Assert
        assertNotNull(response, "The response should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "The status code should be 200");

        Page<UserResponse> result = response.getBody();
        assertNotNull(result, "The response body should not be null");
        assertEquals(2, result.getContent().size(), "It should return 2 users");

        // Verificar el primer usuario
        UserResponse actualUser1 = result.getContent().get(0);
        assertEquals(userResponse.id(), actualUser1.id());
        assertEquals(userResponse.userName(), actualUser1.userName());
        assertEquals(userResponse.name(), actualUser1.name());

        // Verificar metadatos de paginación
        assertEquals(2, result.getTotalElements(), "There should be 2 items in total");
        assertEquals(1, result.getTotalPages(), "There should be 1 page in total");
        assertTrue(result.isFirst(), "There should be 1 page in total");
        assertTrue(result.isLast(), "It should be the last page");

        verify(userService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should return empty page when no users exist")
    void findAll_ShouldReturnEmptyPage_WhenNoUsersExist() {
        // Arrange
        Page<UserResponse> emptyPage = new PageImpl<>(
                Collections.emptyList(),
                PageRequest.of(0, 10, Sort.by("id").ascending()),
                0
        );

        when(userService.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // Act
        ResponseEntity<Page<UserResponse>> response = userController.findAll(0, 10);

        // Assert
        assertNotNull(response, "The response should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "The status code should be 200");

        Page<UserResponse> result = response.getBody();
        assertNotNull(result, "The response body should not be null");
        assertTrue(result.isEmpty(), "The result should be empty");
        assertEquals(0, result.getTotalElements(), "There should be no elements");

        verify(userService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should find a user by ID")
    void findById_ShouldReturnUser_WhenUserExists() {
        when(userService.findById(userId)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.getUserById(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userResponse.id(), response.getBody().id());
        assertEquals(userResponse.userName(), response.getBody().userName());
        assertEquals(userResponse.name(), response.getBody().name());
        assertEquals(userResponse.birthDate(), response.getBody().birthDate());
        assertEquals(userResponse.bookIds(), response.getBody().bookIds());
        verify(userService, times(1)).findById(userId);
    }

    @Test
    @DisplayName("You should successfully create a new user")
    void createUser_ShouldReturnCreatedUser() {
        when(userService.save(any(UserRequest.class))).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.createUser(userRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userResponse.id(), response.getBody().id());
        assertEquals(userResponse.userName(), response.getBody().userName());
        assertEquals(userResponse.name(), response.getBody().name());
        assertEquals(userResponse.birthDate(), response.getBody().birthDate());
        assertEquals(userResponse.bookIds(), response.getBody().bookIds());
        verify(userService, times(1)).save(any(UserRequest.class));
    }

    @Test
    @DisplayName("Should update an existing user")
    void updateUser_ShouldReturnUpdatedUser() {
        when(userService.update(eq(userId), any(UserRequest.class))).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.updateUser(userId, userRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userResponse.id(), response.getBody().id());
        assertEquals(userResponse.userName(), response.getBody().userName());
        assertEquals(userResponse.name(), response.getBody().name());
        assertEquals(userResponse.birthDate(), response.getBody().birthDate());
        assertEquals(userResponse.bookIds(), response.getBody().bookIds());
        verify(userService, times(1)).update(eq(userId), any(UserRequest.class));
    }

    @Test
    @DisplayName("Should delete a user successfully.")
    void deleteUser_ShouldCallDeleteMethod() {
        userController.deleteUser(userId);

        verify(userService, times(1)).delete(userId);
    }

    @Test
    @DisplayName("Should add a book to a user")
    void addBookToUser_ShouldReturnUpdatedUser() {
        UUID bookId = UUID.randomUUID();
        when(userService.addBookToUser(userId, bookId)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.addBookToUser(userId, bookId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userResponse.id(), response.getBody().id());
        assertEquals(userResponse.userName(), response.getBody().userName());
        assertEquals(userResponse.name(), response.getBody().name());
        assertEquals(userResponse.birthDate(), response.getBody().birthDate());
        assertEquals(userResponse.bookIds(), response.getBody().bookIds());
        verify(userService, times(1)).addBookToUser(userId, bookId);
    }

    @Test
    @DisplayName("Should remove a book from a user")
    void removeBookFromUser_ShouldReturnUpdatedUser() {
        UUID bookId = UUID.randomUUID();
        when(userService.removeBookFromUser(userId, bookId)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.removeBookFromUser(userId, bookId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userResponse.id(), response.getBody().id());
        assertEquals(userResponse.userName(), response.getBody().userName());
        assertEquals(userResponse.name(), response.getBody().name());
        assertEquals(userResponse.birthDate(), response.getBody().birthDate());
        assertEquals(userResponse.bookIds(), response.getBody().bookIds());
        verify(userService, times(1)).removeBookFromUser(userId, bookId);
    }
}
