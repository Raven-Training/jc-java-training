package com.raven.training.mapper;

import com.raven.training.persistence.entity.Book;
import com.raven.training.persistence.entity.User;
import com.raven.training.presentation.dto.user.UserRequest;
import com.raven.training.presentation.dto.user.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for IUserMapperTest")
class IUserMapperTest {

    private IUserMapper userMapper;
    
    @Mock
    private Authentication authentication;
    private UserRequest userRequest;
    private User user;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        book1 = Book.builder()
                .id(UUID.randomUUID())
                .title("Libro 1")
                .build();
        
        book2 = Book.builder()
                .id(UUID.randomUUID())
                .title("Libro 2")
                .build();

        userRequest = new UserRequest(
            "juan",
            "Juan Pérez",
            LocalDate.of(1990, 1, 1),
            Arrays.asList(book1.getId(), book2.getId())
        );

        user = User.builder()
                .id(UUID.randomUUID())
                .userName("juan")
                .name("Juan Pérez")
                .birthDate(LocalDate.of(1990, 1, 1))
                .books(new ArrayList<>(Arrays.asList(book1, book2)))
                .build();

        userMapper = IUserMapper.INSTANCE;
    }

    @Test
    @DisplayName("Should map correctly from User to UserResponse")
    void shouldMapUserToUserResponse() {
        UserResponse result = userMapper.toResponse(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.id());
        assertEquals(user.getName(), result.name());
        assertNotNull(result.bookIds());
        assertEquals(2, result.bookIds().size());
        assertTrue(result.bookIds().contains(book1.getId()));
        assertTrue(result.bookIds().contains(book2.getId()));
    }

    @Test
    @DisplayName("Should correctly map a list of User to UserResponse")
    void shouldMapUserListToUserResponseList() {
        List<User> userList = Arrays.asList(user);

        List<UserResponse> result = userMapper.toResponseList(userList);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user.getId(), result.get(0).id());
        assertEquals(user.getName(), result.get(0).name());
        assertEquals(2, result.get(0).bookIds().size());
    }

    @Test
    @DisplayName("Should correctly handle a user without books")
    void shouldHandleUserWithoutBooks() {
        user.setBooks(null);

        UserResponse result = userMapper.toResponse(user);

        assertNotNull(result);
        assertNull(result.bookIds());
    }

    @Test
    @DisplayName("Should correctly map an empty list of books")
    void shouldMapEmptyBookList() {
        user.setBooks(List.of());

        UserResponse result = userMapper.toResponse(user);

        assertNotNull(result);
        assertNotNull(result.bookIds());
        assertTrue(result.bookIds().isEmpty());
    }
    
    @Test
    @DisplayName("Should correctly map a list of books to their IDs")
    void shouldMapBooksToBookIds() {
        List<Book> books = Arrays.asList(book1, book2);

        List<UUID> result = userMapper.mapBooksToBookIds(books);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(book1.getId()));
        assertTrue(result.contains(book2.getId()));
    }
    
    @Test
    @DisplayName("Should correctly handle a null list of books")
    void shouldHandleNullBookList() {
        List<UUID> result = userMapper.mapBooksToBookIds(null);

        assertNull(result);
    }
}
