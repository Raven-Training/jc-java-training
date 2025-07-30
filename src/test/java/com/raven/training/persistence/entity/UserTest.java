package com.raven.training.persistence.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Unit tests for UserTest")
class UserTest {

    private User user;
    private final UUID testId = UUID.randomUUID();
    private final LocalDate testBirthDate = LocalDate.of(1990, 5, 15);
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        book1 = Book.builder()
                .id(UUID.randomUUID())
                .title("Cien años de soledad")
                .author("Gabriel García Márquez")
                .isbn("978-0307474278")
                .publisher("1967")
                .build();

        book2 = Book.builder()
                .id(UUID.randomUUID())
                .title("Don Quijote de la Mancha")
                .author("Miguel de Cervantes")
                .isbn("978-8424119932")
                .publisher("1605")
                .build();

        user = User.builder()
                .id(testId)
                .userName("maria.lopez")
                .name("María López")
                .birthDate(testBirthDate)
                .books(new ArrayList<>(Arrays.asList(book1, book2)))
                .build();
    }


    @Test
    @DisplayName("Should return correct attributes when valid data is used with builder and getters")
    void testUserBuilderAndGetters_WhenValidData_ShouldReturnCorrectAttributes() {
        assertAll(
                () -> assertEquals(testId, user.getId(), "The ID should match"),
                () -> assertEquals("maria.lopez", user.getUserName(), "Username should match"),
                () -> assertEquals("María López", user.getName(), "The full name should match"),
                () -> assertEquals(testBirthDate, user.getBirthDate(), "The date of birth should match"),
                () -> assertNotNull(user.getBooks(), "The book list should not be null"),
                () -> assertEquals(2, user.getBooks().size(), "The book list should contain 2 books"),
                () -> assertTrue(user.getBooks().contains(book1), "The book list should contain book1"),
                () -> assertTrue(user.getBooks().contains(book2), "The book list should contain book2")
        );
    }

    @Test
    @DisplayName("Should initialize books list when no-args constructor is called")
    void testUserNoArgsConstructor_WhenCalled_ShouldInitializeBooksList() {
        User newUser = new User();
        assertNotNull(newUser.getBooks(), "The list of books should not be null when using the no-argument constructor");
        assertTrue(newUser.getBooks().isEmpty(), "The list of books should be empty when using the constructor without arguments");
    }

    @Test
    @DisplayName("Should generate new ID when ID is null in prePersist")
    void testPrePersist_WhenIdIsNull_ShouldGenerateNewId() {
        User newUser = new User();
        assertNull(newUser.getId(), "The ID should be null before prePersist");

        newUser.prePersist();

        assertNotNull(newUser.getId(), "The ID should be generated after prePersist");
    }

    @Test
    @DisplayName("Should not change ID when ID is not null in prePersist")
    void testPrePersist_WhenIdIsNotNull_ShouldNotChangeId() {
        UUID existingId = UUID.randomUUID();
        User newUser = User.builder().id(existingId).build();

        newUser.prePersist();

        assertEquals(existingId, newUser.getId(), "The ID should not change if it is already set");
    }

    @Test
    @DisplayName("Should contain book when book is added")
    void testAddBook_WhenBookAdded_ShouldContainBook() {
        Book newBook = Book.builder()
                .id(UUID.randomUUID())
                .title("La Sombra del Viento")
                .author("Carlos Ruiz Zafón")
                .isbn("978-8408083819")
                .publisher("2001")
                .build();

        user.getBooks().add(newBook);
        assertTrue(user.getBooks().contains(newBook), "The book list should contain the newly added book");
        assertEquals(3, user.getBooks().size(), "The size of the book list should increase");
    }

    @Test
    @DisplayName("Should update books when a new list is set")
    void testSetBooks_WhenNewListSet_ShouldUpdateBooks() {
        List<Book> newList = new ArrayList<>();
        Book anotherBook = Book.builder()
                .id(UUID.randomUUID())
                .title("El amor en los tiempos del cólera")
                .author("Gabriel García Márquez")
                .isbn("978-0307388704")
                .publisher("1985")
                .build();
        newList.add(anotherBook);

        user.setBooks(newList);

        assertAll(
                () -> assertEquals(1, user.getBooks().size(), "The book list should have only one book"),
                () -> assertTrue(user.getBooks().contains(anotherBook), "The book list should contain the new book"),
                () -> assertFalse(user.getBooks().contains(book1), "The book list should not contain old books")
        );
    }

    @Test
    @DisplayName("Should be equal when IDs match")
    void testEquals_WhenSameId_ShouldBeEqual() {
        User anotherUser = User.builder()
                .id(testId)
                .userName("otro.usuario")
                .name("Otro Nombre")
                .build();

        assertEquals(user, anotherUser, "Users with the same ID should be considered equal");
    }

    @Test
    @DisplayName("Should not be equal when IDs differ")
    void testEquals_WhenDifferentId_ShouldNotBeEqual() {
        User differentUser = User.builder()
                .id(UUID.randomUUID())
                .userName("juan.perez")
                .name("Juan Pérez")
                .build();

        assertNotEquals(user, differentUser, "Users with different IDs should not be the same");
    }

    @Test
    @DisplayName("Should have same hash code when IDs match")
    void testHashCode_WhenSameId_ShouldHaveSameHashCode() {
        User anotherUser = User.builder()
                .id(testId)
                .userName("otro.usuario")
                .name("Otro Nombre")
                .build();

        assertEquals(user.hashCode(), anotherUser.hashCode(), "Users with the same ID should have the same hash code.");
    }
}
