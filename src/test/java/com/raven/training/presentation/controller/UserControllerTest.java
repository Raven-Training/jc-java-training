package com.raven.training.presentation.controller;

import com.raven.training.presentation.dto.pagination.CustomPageableResponse;
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
    @DisplayName("Should return a custom paginated response with users successfully")
    void findAll_ShouldReturnCustomPageableResponseWithUsers() {
        UserResponse userResponse2 = new UserResponse(
                UUID.randomUUID(),
                "anotheruser",
                "Another User",
                LocalDate.of(1995, 5, 15),
                List.of()
        );

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<UserResponse> expectedPage = new PageImpl<>(
                Arrays.asList(userResponse, userResponse2),
                pageable,
                2
        );

        when(userService.findAll(any(Pageable.class))).thenReturn(expectedPage);

        ResponseEntity<CustomPageableResponse<UserResponse>> response = userController.findAll(0, 10);

        assertNotNull(response, "The response should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "The status code should be 200");

        CustomPageableResponse<UserResponse> result = response.getBody();
        assertNotNull(result, "The response body should not be null");

        assertFalse(result.page().isEmpty(), "The result page should not be empty");
        assertEquals(2, result.page().size(), "It should return 2 users");
        assertEquals(2, result.total_count(), "There should be 2 items in total");
        assertEquals(1, result.total_pages(), "There should be 1 page in total");
        assertEquals(1, result.current_page(), "The current page should be 1");
        assertNull(result.previous_page(), "The previous page should be null");
        assertNull(result.next_page(), "The next page should be null");

        UserResponse actualUser1 = result.page().get(0);
        assertEquals(userResponse.id(), actualUser1.id());
        assertEquals(userResponse.userName(), actualUser1.userName());
        assertEquals(userResponse.name(), actualUser1.name());

        verify(userService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should return an empty custom paginated response when no users exist")
    void findAll_ShouldReturnEmptyCustomPageableResponse_WhenNoUsersExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<UserResponse> emptyPage = new PageImpl<>(
                Collections.emptyList(),
                pageable,
                0
        );

        when(userService.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // Act
        ResponseEntity<CustomPageableResponse<UserResponse>> response = userController.findAll(0, 10);

        // Assert
        assertNotNull(response, "The response should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "The status code should be 200");

        // Obtenemos el cuerpo de la respuesta como nuestro record CustomPageableResponse
        CustomPageableResponse<UserResponse> result = response.getBody();
        assertNotNull(result, "The response body should not be null");

        // Verificamos las propiedades del record
        assertTrue(result.page().isEmpty(), "The result page should be empty");
        assertEquals(0, result.total_count(), "There should be no elements in total");
        assertEquals(0, result.total_pages(), "There should be 0 pages in total");
        assertEquals(1, result.current_page(), "The current page should be 1");
        assertNull(result.previous_page(), "The previous page should be null");
        assertNull(result.next_page(), "The next page should be null");

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

    @Test
    void getCurrentUser_ReturnsCurrentUserAndOkStatus() {
        when(userService.getCurrentUser()).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.getCurrentUser();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
    }
}
