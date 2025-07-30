package com.raven.training.persistence.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for BookTest")
class BookTest {

    private Book book;
    private final UUID testBookId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .id(testBookId)
                .gender("Fantasy")
                .author("J.R.R. Tolkien")
                .image("image_url.jpg")
                .title("The Hobbit")
                .subtitle("There and Back Again")
                .publisher("George Allen & Unwin")
                .year("1937")
                .pages(310)
                .isbn("978-0345339683")
                .users(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should return correct attributes")
    void testBookBuilderAndGetters_WhenValidData_ShouldReturnCorrectAttributes() {
        assertAll(
                () -> assertEquals(testBookId, book.getId(), "ID should match"),
                () -> assertEquals("Fantasy", book.getGender(), "Gender should match"),
                () -> assertEquals("J.R.R. Tolkien", book.getAuthor(), "Author should match"),
                () -> assertEquals("image_url.jpg", book.getImage(), "Image should match"),
                () -> assertEquals("The Hobbit", book.getTitle(), "Title should match"),
                () -> assertEquals("There and Back Again", book.getSubtitle(), "Subtitle should match"),
                () -> assertEquals("George Allen & Unwin", book.getPublisher(), "Publisher should match"),
                () -> assertEquals("1937", book.getYear(), "Year should match"),
                () -> assertEquals(310, book.getPages(), "Pages should match"),
                () -> assertEquals("978-0345339683", book.getIsbn(), "ISBN should match"),
                () -> assertNotNull(book.getUsers(), "Users list should not be null"),
                () -> assertTrue(book.getUsers().isEmpty(), "Users list should be empty initially")
        );
    }

    @Test
    @DisplayName("Should generate ID when ID is null")
    void testPrePersist_WhenIdIsNull_ShouldGenerateId() {
        Book newBook = new Book();
        assertNull(newBook.getId(), "ID should be null before prePersist");

        newBook.prePersist();

        assertNotNull(newBook.getId(), "ID should be generated after prePersist");
    }

    @Test
    @DisplayName("Should keep the existing Id when it's not null")
    void testPrePersist_WhenIdIsNotNull_ShouldKeepExistingId() {
        UUID existingId = UUID.randomUUID();
        book.setId(existingId);

        book.prePersist();

        assertEquals(existingId, book.getId(), "ID should not change after prePersist if already set");
    }

    @Test
    @DisplayName("Should be equal when IDs match")
    void testEqualsAndHashCode_WhenIdsMatch_ShouldBeEqual() {
        Book sameBook = Book.builder()
                .id(testBookId)
                .gender("Sci-Fi")
                .author("Someone Else")
                .build();

        Book differentBook = Book.builder()
                .id(UUID.randomUUID())
                .gender("Fantasy")
                .author("J.R.R. Tolkien")
                .build();

        assertAll(
                () -> assertEquals(book, sameBook, "Books should be equal when IDs match"),
                () -> assertEquals(book.hashCode(), sameBook.hashCode(), "Hash codes should match for equal objects"),
                () -> assertNotEquals(book, differentBook, "Books should not be equal when IDs differ")
        );
    }

    @Test
    @DisplayName("Should contain key attributes when toString is called")
    void testToString_WhenCalled_ShouldContainKeyAttributes() {
        String toStringResult = book.toString();

        assertAll(
                () -> assertTrue(toStringResult.contains("id=" + testBookId), "ToString should contain ID"),
                () -> assertTrue(toStringResult.contains("title=The Hobbit"), "ToString should contain title"),
                () -> assertTrue(toStringResult.contains("author=J.R.R. Tolkien"), "ToString should contain author"),
                () -> assertTrue(toStringResult.contains("isbn=978-0345339683"), "ToString should contain ISBN")
        );
    }

    @Test
    @DisplayName("Should be empty when users list is initialized")
    void testUsersList_WhenInitialized_ShouldBeEmpty() {
        assertNotNull(book.getUsers(), "Users list should not be null");
        assertTrue(book.getUsers().isEmpty(), "Users list should be empty on initialization");
    }

    @Test
    @DisplayName("Should contain user when user is added to book")
    void testAddUserToBook_WhenUserAdded_ShouldContainUser() {
        User user = User.builder().id(UUID.randomUUID()).build();
        book.getUsers().add(user);

        assertFalse(book.getUsers().isEmpty(), "Users list should not be empty after adding a user");
        assertTrue(book.getUsers().contains(user), "Users list should contain the added user");
    }
}
