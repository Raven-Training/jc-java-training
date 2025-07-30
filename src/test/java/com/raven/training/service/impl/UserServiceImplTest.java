package com.raven.training.service.impl;

import com.raven.training.exception.error.BookAlreadyInCollectionException;
import com.raven.training.exception.error.BookNotFoundException;
import com.raven.training.exception.error.BookNotInCollectionException;
import com.raven.training.exception.error.UserNotFoundException;
import com.raven.training.mapper.IUserMapper;
import com.raven.training.persistence.entity.Book;
import com.raven.training.persistence.entity.User;
import com.raven.training.persistence.repository.IBookRepository;
import com.raven.training.persistence.repository.IUserRepository;
import com.raven.training.presentation.dto.user.UserRequest;
import com.raven.training.presentation.dto.user.UserResponse;
import com.raven.training.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the class UserServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for UserServiceImpl")
class UserServiceImplTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IBookRepository bookRepository;

    @Mock
    private IUserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Book testBook;
    private final UUID userId = UUID.randomUUID();
    private final UUID bookId = UUID.randomUUID();
    private final LocalDate testBirthDate = LocalDate.of(1990, 1, 1);

    @BeforeEach
    void setUp() {
        testBook = Book.builder()
                .id(bookId)
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .build();

        testUser = User.builder()
                .id(userId)
                .userName("testuser")
                .name("Test User")
                .birthDate(testBirthDate)
                .books(new ArrayList<>(Collections.singletonList(testBook)))
                .build();
    }

    // =========================================
    // Tests for findAll()
    // =========================================

    @Test
    @DisplayName("Should return a page of users when they exist")
    void findAll_WhenUsersExist_ShouldReturnPageOfUsers() {
        List<User> userList = Collections.singletonList(testUser);
        UserResponse userResponse = new UserResponse(
                userId, "testuser", "Test User", testBirthDate, Collections.singletonList(bookId));

        User testUser2 = User.builder()
                .id(UUID.randomUUID())
                .userName("anotheruser")
                .name("Another User")
                .birthDate(testBirthDate)
                .books(new ArrayList<>())
                .build();

        List<User> allUsers = Arrays.asList(testUser, testUser2);
        Pageable pageable = PageRequest.of(0, 1, Sort.by("userName").ascending()); // Página de tamaño 1

        Page<User> userPage = new PageImpl<>(
                Collections.singletonList(testUser),
                pageable,
                2
        );


        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        Page<UserResponse> result = userService.findAll(pageable);

        assertNotNull(result, "El resultado no debería ser nulo");
        assertEquals(1, result.getContent().size(), "Debería devolver 1 usuario");
        assertEquals(userId, result.getContent().get(0).id(), "El ID del usuario debería coincidir");
        assertEquals(2, result.getTotalElements(), "El total de elementos debería ser 2");
        assertEquals(2, result.getTotalPages(), "Debería haber 2 páginas en total");
        assertTrue(result.isFirst(), "Debería ser la primera página");
        assertTrue(result.hasNext(), "Debería tener una página siguiente");
        assertFalse(result.isLast(), "No debería ser la última página");

        verify(userRepository, times(1)).findAll(any(Pageable.class));
        verify(userMapper, times(1)).toResponse(any(User.class));
    }

    @Test
    @DisplayName("Should return empty page when no users exist")
    void findAll_WhenNoUsersExist_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("userName").ascending());
        Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        Page<UserResponse> result = userService.findAll(pageable);

        assertNotNull(result, "El resultado no debería ser nulo");
        assertTrue(result.isEmpty(), "El resultado debería estar vacío");
        assertEquals(0, result.getTotalElements(), "No debería haber elementos");
        assertEquals(0, result.getTotalPages(), "Debería haber 0 páginas");

        verify(userRepository, times(1)).findAll(any(Pageable.class));
        verify(userMapper, never()).toResponse(any(User.class));
    }

    // =========================================
    // Tests for findById()
    // =========================================

    @Test
    @DisplayName("Should return a user when it exists with the provided ID")
    void findById_WhenUserExists_ShouldReturnUser() {
        UserResponse expectedResponse = new UserResponse(
                userId, "testuser", "Test User", testBirthDate, Collections.singletonList(bookId));
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(expectedResponse);

        var result = userService.findById(userId);

        assertNotNull(result, "User should not be null");
        assertEquals(userId, result.id(), "The user ID should match");
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toResponse(testUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when the user does not exist")
    void findById_WhenUserNotExists_ShouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.findById(nonExistentId),
                "The user ID should match");
        verify(userRepository, times(1)).findById(nonExistentId);
        verify(userMapper, never()).toResponse(any(User.class));
    }

    // =========================================
    // Tests for save()
    // =========================================

    @Test
    @DisplayName("Should save a new user correctly")
    void save_WithValidData_ShouldSaveAndReturnUser() {
        UserRequest userRequest = new UserRequest(
                "newuser", "New User", testBirthDate, Collections.singletonList(bookId));
        
        User newUser = User.builder()
                .userName(userRequest.userName())
                .name(userRequest.name())
                .birthDate(userRequest.birthDate())
                .books(new ArrayList<>(Collections.singletonList(testBook)))
                .build();
        
        UserResponse expectedResponse = new UserResponse(
                userId, "newuser", "New User", testBirthDate, Collections.singletonList(bookId));

        when(bookRepository.findAllByIdIn(userRequest.bookIds()))
                .thenReturn(Collections.singletonList(testBook));
        when(userMapper.toEntity(userRequest)).thenReturn(newUser);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(userId);
            return u;
        });
        when(userMapper.toResponse(any(User.class))).thenReturn(expectedResponse);

        var result = userService.save(userRequest);

        assertNotNull(result, "The saved user should not be null");
        assertEquals(userRequest.userName(), result.userName(), "Username should match");
        verify(bookRepository, times(1)).findAllByIdIn(userRequest.bookIds());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when not all books are found for the given IDs")
    void save_WhenSomeBooksNotFound_ShouldThrowUserNotFoundException() {
        UUID existingBookId = UUID.randomUUID();
        UUID nonExistingBookId = UUID.randomUUID();

        UserRequest userRequest = new UserRequest(
                "testuser",
                "Test User",
                testBirthDate,
                Arrays.asList(existingBookId, nonExistingBookId)
        );

        Book existingBook = Book.builder()
                .id(existingBookId)
                .title("Existing Book")
                .author("Author A")
                .isbn("1234567890")
                .build();

        when(bookRepository.findAllByIdIn(userRequest.bookIds()))
                .thenReturn(Collections.singletonList(existingBook));

        assertThrows(UserNotFoundException.class,
                () -> userService.save(userRequest),
                "Should throw UserNotFoundException when not all books are found");

        verify(bookRepository, times(1)).findAllByIdIn(userRequest.bookIds());
        verify(userMapper, never()).toEntity(any(UserRequest.class));
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toResponse(any(User.class));
    }

    // =========================================
    // Tests for update()
    // =========================================

    @Test
    @DisplayName("Should update an existing user successfully")
    void update_WhenUserExists_ShouldUpdateAndReturnUser() {
        UserRequest updateRequest = new UserRequest(
                "updateduser", "Updated User", testBirthDate.plusYears(1), Collections.singletonList(bookId));
        
        User updatedUser = User.builder()
                .id(userId)
                .userName(updateRequest.userName())
                .name(updateRequest.name())
                .birthDate(updateRequest.birthDate())
                .books(new ArrayList<>(Collections.singletonList(testBook)))
                .build();
        
        UserResponse expectedResponse = new UserResponse(
                userId, "updateduser", "Updated User", testBirthDate.plusYears(1), 
                Collections.singletonList(bookId));

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(expectedResponse);

        var result = userService.update(userId, updateRequest);

        assertNotNull(result, "The updated user should not be null");
        assertEquals(updateRequest.userName(), result.userName(), "The username should be updated");
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update an existing user successfully with all fields")
    void update_WhenUserExists_ShouldUpdateAllFieldsAndReturnUser() {
        UserRequest updateRequest = new UserRequest(
                "updateduser", "Updated User Name", testBirthDate.plusYears(1), Collections.singletonList(bookId));

        User initialUser = User.builder()
                .id(userId)
                .userName("olduser")
                .name("Old Name")
                .birthDate(testBirthDate)
                .books(new ArrayList<>())
                .build();

        Book bookToUpdate = Book.builder()
                .id(bookId)
                .title("Updated Book Title")
                .author("Author B")
                .isbn("9876543210")
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .userName(updateRequest.userName())
                .name(updateRequest.name())
                .birthDate(updateRequest.birthDate())
                .books(new ArrayList<>(Collections.singletonList(bookToUpdate)))
                .build();

        UserResponse expectedResponse = new UserResponse(
                userId, updateRequest.userName(), updateRequest.name(), updateRequest.birthDate(),
                Collections.singletonList(bookId));

        when(userRepository.findById(userId)).thenReturn(Optional.of(initialUser));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookToUpdate));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(expectedResponse);

        UserResponse result = userService.update(userId, updateRequest);

        assertNotNull(result, "The updated user should not be null");
        assertEquals(updateRequest.userName(), result.userName(), "The username should be updated");
        assertEquals(updateRequest.name(), result.name(), "The name should be updated");
        assertEquals(updateRequest.birthDate(), result.birthDate(), "The birth date should be updated");
        assertTrue(result.bookIds().contains(bookId), "The book should be added to the collection");
        assertEquals(1, result.bookIds().size(), "There should be one book in the collection");

        verify(userRepository, times(1)).findById(userId);
        verify(bookRepository, times(1)).findById(bookId); // Verificamos que se buscó el libro
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toResponse(any(User.class));
    }

    @Test
    @DisplayName("Should update only provided fields when others are null or empty")
    void update_WhenSomeFieldsAreNullOrEmpty_ShouldUpdateOnlyProvidedFields() {
        UserRequest updateRequest = new UserRequest(
                "newUsername", null, null, Collections.singletonList(bookId));

        User initialUser = User.builder()
                .id(userId)
                .userName("oldUsername")
                .name("Old Name")
                .birthDate(LocalDate.of(2000, 1, 1))
                .books(new ArrayList<>())
                .build();

        Book bookToAdd = Book.builder().id(bookId).title("Test Book").author("Test Author").isbn("123").build();

        UserResponse expectedResponse = new UserResponse(
                userId, "newUsername", "Old Name", LocalDate.of(2000, 1, 1), Collections.singletonList(bookId));

        when(userRepository.findById(userId)).thenReturn(Optional.of(initialUser));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookToAdd));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            assertEquals("newUsername", userToSave.getUserName(), "Username should be updated");
            assertEquals("Old Name", userToSave.getName(), "Name should remain unchanged");
            assertEquals(LocalDate.of(2000, 1, 1), userToSave.getBirthDate(), "BirthDate should remain unchanged");
            assertTrue(userToSave.getBooks().contains(bookToAdd), "Book should be added");
            return userToSave;
        });
        when(userMapper.toResponse(any(User.class))).thenReturn(expectedResponse);

        UserResponse result = userService.update(userId, updateRequest);

        assertNotNull(result);
        assertEquals("newUsername", result.userName());
        assertEquals("Old Name", result.name());
        assertEquals(LocalDate.of(2000, 1, 1), result.birthDate());
        assertTrue(result.bookIds().contains(bookId));
        assertEquals(1, result.bookIds().size());

        verify(userRepository, times(1)).findById(userId);
        verify(bookRepository, times(1)).findById(bookId);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toResponse(any(User.class));
    }

    @Test
    @DisplayName("Should clear existing books and add new ones when bookIds is provided")
    void update_WhenBookIdsProvided_ShouldClearAndAddBooks() {
        UUID initialBookId = UUID.randomUUID();
        Book initialBook = Book.builder().id(initialBookId).title("Initial Book").build();

        User existingUserWithBooks = User.builder()
                .id(userId)
                .userName("userWithBooks")
                .name("User With Books")
                .birthDate(testBirthDate)
                .books(new ArrayList<>(Collections.singletonList(initialBook)))
                .build();

        UUID newBookId = UUID.randomUUID();
        Book newBook = Book.builder().id(newBookId).title("New Book").build();

        UserRequest updateRequest = new UserRequest(
                "userWithBooks", "User With Books", testBirthDate, Collections.singletonList(newBookId));

        UserResponse expectedResponse = new UserResponse(
                userId, "userWithBooks", "User With Books", testBirthDate, Collections.singletonList(newBookId));

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUserWithBooks));
        when(bookRepository.findById(newBookId)).thenReturn(Optional.of(newBook));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            assertFalse(userToSave.getBooks().contains(initialBook), "Initial book should be removed");
            assertTrue(userToSave.getBooks().contains(newBook), "New book should be added");
            return userToSave;
        });
        when(userMapper.toResponse(any(User.class))).thenReturn(expectedResponse);

        UserResponse result = userService.update(userId, updateRequest);

        assertNotNull(result);
        assertTrue(result.bookIds().contains(newBookId), "The new book ID should be in the response");
        assertFalse(result.bookIds().contains(initialBookId), "The old book ID should not be in the response");
        assertEquals(1, result.bookIds().size());

        verify(userRepository, times(1)).findById(userId);
        verify(bookRepository, times(1)).findById(newBookId);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toResponse(any(User.class));
    }

    @Test
    @DisplayName("Should not add books if bookRepository.findById returns empty for an ID")
    void update_WhenSomeBookIdsNotFoundInRepository_ShouldOnlyAddExistingBooks() {
        UUID bookId1 = UUID.randomUUID();
        UUID bookId2 = UUID.randomUUID();
        Book book1 = Book.builder().id(bookId1).title("Book 1").build();

        User existingUser = User.builder()
                .id(userId)
                .userName("userTest")
                .name("Test User")
                .birthDate(testBirthDate)
                .books(new ArrayList<>())
                .build();

        UserRequest updateRequest = new UserRequest(
                "userTest", "Test User", testBirthDate, Arrays.asList(bookId1, bookId2));

        UserResponse expectedResponse = new UserResponse(
                userId, "userTest", "Test User", testBirthDate, Collections.singletonList(bookId1));

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(bookRepository.findById(bookId1)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(bookId2)).thenReturn(Optional.empty());

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            assertTrue(userToSave.getBooks().contains(book1), "Book 1 should be added");
            assertEquals(1, userToSave.getBooks().size(), "Only Book 1 should be added");
            return userToSave;
        });
        when(userMapper.toResponse(any(User.class))).thenReturn(expectedResponse);

        UserResponse result = userService.update(userId, updateRequest);

        assertNotNull(result);
        assertTrue(result.bookIds().contains(bookId1));
        assertFalse(result.bookIds().contains(bookId2));
        assertEquals(1, result.bookIds().size());

        verify(userRepository, times(1)).findById(userId);
        verify(bookRepository, times(1)).findById(bookId1);
        verify(bookRepository, times(1)).findById(bookId2);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toResponse(any(User.class));
    }

    @Test
    @DisplayName("Should not modify books when bookIds list is empty (current logic)")
    void update_WhenBookIdsIsEmpty_ShouldNotClearBooksGivenCurrentLogic() {
        UUID initialBookId = UUID.randomUUID();
        Book initialBook = Book.builder().id(initialBookId).title("Initial Book").build();
        User existingUserWithBooks = User.builder()
                .id(userId)
                .userName("userWithBooks")
                .name("User With Books")
                .birthDate(testBirthDate)
                .books(new ArrayList<>(Collections.singletonList(initialBook)))
                .build();

        UserRequest updateRequest = new UserRequest(
                "userWithBooks", "User With Books", testBirthDate, Collections.emptyList());

        UserResponse expectedResponse = new UserResponse(
                userId, "userWithBooks", "User With Books", testBirthDate, Collections.singletonList(initialBookId));

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUserWithBooks));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            assertTrue(userToSave.getBooks().contains(initialBook), "Initial book should still be present");
            assertEquals(1, userToSave.getBooks().size());
            return userToSave;
        });
        when(userMapper.toResponse(any(User.class))).thenReturn(expectedResponse);

        UserResponse result = userService.update(userId, updateRequest);

        assertNotNull(result);
        assertFalse(result.bookIds().isEmpty(), "Response bookIds should not be empty");
        assertTrue(result.bookIds().contains(initialBookId), "Response should still contain initial book ID");
        assertEquals(1, result.bookIds().size());

        verify(userRepository, times(1)).findById(userId);
        verify(bookRepository, never()).findById(any(UUID.class));
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toResponse(any(User.class));
    }

    @Test
    @DisplayName("Should not modify books when bookIds is null")
    void update_WhenBookIdsIsNull_ShouldNotModifyBooks() {
        UUID initialBookId = UUID.randomUUID();
        Book initialBook = Book.builder().id(initialBookId).title("Initial Book").build();
        User existingUserWithBooks = User.builder()
                .id(userId)
                .userName("userWithBooks")
                .name("User With Books")
                .birthDate(testBirthDate)
                .books(new ArrayList<>(Collections.singletonList(initialBook)))
                .build();

        UserRequest updateRequest = new UserRequest(
                "userWithBooks", "User With Books", testBirthDate, null);

        UserResponse expectedResponse = new UserResponse(
                userId, "userWithBooks", "User With Books", testBirthDate, Collections.singletonList(initialBookId));

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUserWithBooks));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            assertTrue(userToSave.getBooks().contains(initialBook), "Initial book should still be present");
            assertEquals(1, userToSave.getBooks().size());
            return userToSave;
        });
        when(userMapper.toResponse(any(User.class))).thenReturn(expectedResponse);

        UserResponse result = userService.update(userId, updateRequest);

        assertNotNull(result);
        assertTrue(result.bookIds().contains(initialBookId), "Response should still contain initial book ID");
        assertEquals(1, result.bookIds().size());

        verify(userRepository, times(1)).findById(userId);
        verify(bookRepository, never()).findById(any(UUID.class));
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toResponse(any(User.class));
    }

    // =========================================
    // Tests for delete()
    // =========================================

    @Test
    @DisplayName("Should delete an existing user successfully?")
    void delete_WhenUserExists_ShouldDeleteUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        assertDoesNotThrow(() -> userService.delete(userId),
                "Should not throw any exceptions");
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(testUser);
    }

    // =========================================
    // Tests for addBookToUser()
    // =========================================

    @Test
    @DisplayName("Should add a book to the user's collection")
    void addBookToUser_WithValidIds_ShouldAddBookToUser() {
        UUID newBookId = UUID.randomUUID();
        Book newBook = Book.builder()
                .id(newBookId)
                .title("Clean Architecture")
                .author("Robert C. Martin")
                .isbn("9780134494166")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(newBookId)).thenReturn(Optional.of(newBook));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(
                new UserResponse(userId, "testuser", "Test User", testBirthDate, 
                        Arrays.asList(bookId, newBookId)));

        var result = userService.addBookToUser(userId, newBookId);

        assertNotNull(result, "The answer should not be null");
        assertEquals(2, result.bookIds().size(), "Should have two books in the collection");
        assertTrue(result.bookIds().contains(newBookId), "The new book should be in the collection");
        verify(userRepository, times(1)).findById(userId);
        verify(bookRepository, times(1)).findById(newBookId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BookAlreadyInCollectionException when adding a duplicate book")
    void addBookToUser_WithDuplicateBook_ShouldThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        assertThrows(BookAlreadyInCollectionException.class,
                () -> userService.addBookToUser(userId, bookId),
                "Should throw BookAlreadyInCollectionException");
        verify(userRepository, times(1)).findById(userId);
        verify(bookRepository, times(1)).findById(bookId);
        verify(userRepository, never()).save(any(User.class));
    }

    // =========================================
    // Tests for removeBookFromUser()
    // =========================================

    @Test
    @DisplayName("Should remove a book from the user's collection")
    void removeBookFromUser_WithValidIds_ShouldRemoveBookFromUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(
                new UserResponse(userId, "testuser", "Test User", testBirthDate, Collections.emptyList()));

        var result = userService.removeBookFromUser(userId, bookId);

        assertNotNull(result, "The answer should not be null");
        assertTrue(result.bookIds().isEmpty(), "The book collection should be empty");
        verify(userRepository, times(1)).findById(userId);
        verify(bookRepository, times(1)).existsById(bookId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BookNotInCollectionException when deleting a book that is not in the collection")
    void removeBookFromUser_WithBookNotInCollection_ShouldThrowException() {
        UUID nonExistentBookId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(bookRepository.existsById(nonExistentBookId)).thenReturn(true);

        assertThrows(BookNotInCollectionException.class,
                () -> userService.removeBookFromUser(userId, nonExistentBookId),
                "Should throw BookNotInCollectionException");
        verify(userRepository, times(1)).findById(userId);
        verify(bookRepository, times(1)).existsById(nonExistentBookId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when removing book from non-existent user")
    void removeBookFromUser_WhenUserNotExists_ShouldThrowUserNotFoundException() {
        UUID nonExistentUserId = UUID.randomUUID();

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.removeBookFromUser(nonExistentUserId, bookId),
                "Should throw UserNotFoundException when user does not exist");


        verify(userRepository, times(1)).findById(nonExistentUserId);
        verify(bookRepository, never()).existsById(any(UUID.class));
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @Test
    @DisplayName("Should throw BookNotFoundException when book to remove does not exist in repository")
    void removeBookFromUser_WhenBookNotExistsInRepository_ShouldThrowBookNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        UUID nonExistentBookId = UUID.randomUUID();
        when(bookRepository.existsById(nonExistentBookId)).thenReturn(false);

        assertThrows(BookNotFoundException.class,
                () -> userService.removeBookFromUser(userId, nonExistentBookId),
                "Should throw BookNotFoundException when book does not exist in repository");

        verify(userRepository, times(1)).findById(userId);
        verify(bookRepository, times(1)).existsById(nonExistentBookId);
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @Test
    @DisplayName("Should throw BookNotInCollectionException when user's book collection is empty")
    void removeBookFromUser_WhenUserBookCollectionIsEmpty_ShouldThrowBookNotInCollectionException() {
        User userWithEmptyBooks = User.builder()
                .id(userId)
                .userName(testUser.getUserName())
                .name(testUser.getName())
                .birthDate(testUser.getBirthDate())
                .books(new ArrayList<>())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithEmptyBooks));
        when(bookRepository.existsById(bookId)).thenReturn(true);

        assertThrows(BookNotInCollectionException.class,
                () -> userService.removeBookFromUser(userId, bookId),
                "Should throw BookNotInCollectionException when user's book collection is empty");

        verify(userRepository, times(1)).findById(userId);
        verify(bookRepository, times(1)).existsById(bookId);
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toResponse(any(User.class));
    }
}
