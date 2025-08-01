package com.raven.training.presentation.dto.bookexternal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.raven.training.presentation.dto.book.bookexternal.OpenLibraryBookDTO;
import com.raven.training.util.deserializer.IdentifierDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit tests for OpenLibraryBookDTO")
class OpenLibraryBookDTOTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Map.class, new IdentifierDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    @DisplayName("Should deserialize JSON with @JsonAnySetter correctly")
    void setBook_WithJsonAnySetter_ShouldSetBookInMap() {
        OpenLibraryBookDTO dto = new OpenLibraryBookDTO();
        OpenLibraryBookDTO.BookData bookData = new OpenLibraryBookDTO.BookData();
        bookData.setTitle("Test Book");

        dto.setBook("OL1M", bookData);

        assertNotNull(dto.getBooks(), "The books map should not be null");
        assertEquals(1, dto.getBooks().size(), "It should contain one book");
        assertTrue(dto.getBooks().containsKey("OL1M"), "It should contain the correct key");
        assertEquals("Test Book", dto.getBooks().get("OL1M").getTitle(), "The title should match");
    }

    @Test
    @DisplayName("Should deserialize JSON with custom IdentifierDeserializer for identifiers field")
    void deserialize_WithIdentifiers_ShouldUseCustomDeserializer() throws JsonProcessingException {
        String json = "{" +
                "\"OL1M\": {" +
                "  \"title\": \"Test Book\"," +
                "  \"identifiers\": {" +
                "    \"isbn_10\": [\"1234567890\"]," +
                "    \"lccn\": [\"12345678\"]," +
                "    \"openlibrary\": [\"OL1M\"]" +
                "  }" +
                "}" +
                "}";

        OpenLibraryBookDTO result = objectMapper.readValue(json, OpenLibraryBookDTO.class);
        OpenLibraryBookDTO.BookData bookData = result.getBooks().get("OL1M");

        assertNotNull(bookData, "The book data should not be null");
        assertNotNull(bookData.getIdentifiers(), "The identifiers field should not be null");
        assertEquals(3, bookData.getIdentifiers().size(), "It should contain 3 identifiers");
        assertEquals("1234567890", bookData.getIdentifiers().get("isbn_10"), "The ISBN-10 should match");
        assertEquals("12345678", bookData.getIdentifiers().get("lccn"), "The LCCN should match");
        assertEquals("OL1M", bookData.getIdentifiers().get("openlibrary"), "The OpenLibrary ID should match");
    }

    @Test
    @DisplayName("Should handle empty identifiers object")
    void deserialize_WithEmptyIdentifiers_ShouldReturnEmptyMap() throws JsonProcessingException {
        String json = "{" +
                "\"OL1M\": {" +
                "  \"title\": \"Test Book\"," +
                "  \"identifiers\": {}" +
                "}" +
                "}";

        OpenLibraryBookDTO result = objectMapper.readValue(json, OpenLibraryBookDTO.class);
        OpenLibraryBookDTO.BookData bookData = result.getBooks().get("OL1M");

        assertNotNull(bookData, "The book data should not be null");
        assertNotNull(bookData.getIdentifiers(), "The identifiers field should not be null");
        assertTrue(bookData.getIdentifiers().isEmpty(), "The identifiers map should be empty");
    }

    @Test
    @DisplayName("Should test @Data toString, equals, and hashCode for BookData")
    void bookData_ShouldHaveWorkingToStringEqualsAndHashCode() {
        OpenLibraryBookDTO.BookData bookData1 = new OpenLibraryBookDTO.BookData();
        bookData1.setTitle("Test Book");

        OpenLibraryBookDTO.BookData bookData2 = new OpenLibraryBookDTO.BookData();
        bookData2.setTitle("Test Book");

        OpenLibraryBookDTO.BookData differentBook = new OpenLibraryBookDTO.BookData();
        differentBook.setTitle("Different Book");

        assertAll(
                () -> assertNotNull(bookData1.toString(), "toString() should not return null"),
                () -> assertEquals(bookData1, bookData2, "Objects with the same values should be equal"),
                () -> assertNotEquals(bookData1, differentBook, "Objects with different values should not be equal"),
                () -> assertEquals(bookData1.hashCode(), bookData2.hashCode(), "The hashCodes of equal objects should match")
        );
    }

    @Test
    @DisplayName("Should test @Data getters and setters for BookData")
    void bookData_ShouldHaveWorkingGettersAndSetters() {
        OpenLibraryBookDTO.BookData bookData = new OpenLibraryBookDTO.BookData();
        String title = "Test Book";
        String subtitle = "Subtitle";
        String publishDate = "2023-01-01";
        Integer numberOfPages = 100;

        OpenLibraryBookDTO.Publisher publisher = new OpenLibraryBookDTO.Publisher();
        publisher.setName("Test Publisher");

        OpenLibraryBookDTO.Author author = new OpenLibraryBookDTO.Author();
        author.setName("Test Author");

        Map<String, Object> identifiers = Map.of("isbn", "1234567890");

        bookData.setTitle(title);
        bookData.setSubtitle(subtitle);
        bookData.setPublishDate(publishDate);
        bookData.setNumberOfPages(numberOfPages);
        bookData.setPublishers(List.of(publisher));
        bookData.setAuthors(List.of(author));
        bookData.setIdentifiers(identifiers);

        assertAll(
                () -> assertEquals(title, bookData.getTitle(), "The title should match"),
                () -> assertEquals(subtitle, bookData.getSubtitle(), "The subtitle should match"),
                () -> assertEquals(publishDate, bookData.getPublishDate(), "The publish date should match"),
                () -> assertEquals(numberOfPages, bookData.getNumberOfPages(), "The number of pages should match"),
                () -> assertEquals(1, bookData.getPublishers().size(), "It should have 1 publisher"),
                () -> assertEquals("Test Publisher", bookData.getPublishers().get(0).getName(), "The publisher's name should match"),
                () -> assertEquals(1, bookData.getAuthors().size(), "It should have 1 author"),
                () -> assertEquals("Test Author", bookData.getAuthors().get(0).getName(), "The author's name should match"),
                () -> assertEquals(identifiers, bookData.getIdentifiers(), "The identifiers should match")
        );
    }

    @Test
    @DisplayName("Should test @Data getters and setters for Publisher")
    void publisher_ShouldHaveWorkingGettersAndSetters() {
        OpenLibraryBookDTO.Publisher publisher = new OpenLibraryBookDTO.Publisher();
        String name = "Test Publisher";

        publisher.setName(name);

        assertEquals(name, publisher.getName(), "The publisher's name should match");
    }

    @Test
    @DisplayName("Should test @Data getters and setters for Author")
    void author_ShouldHaveWorkingGettersAndSetters() {
        OpenLibraryBookDTO.Author author = new OpenLibraryBookDTO.Author();
        String name = "Test Author";

        author.setName(name);

        assertEquals(name, author.getName(), "The author's name should match");
    }
}
