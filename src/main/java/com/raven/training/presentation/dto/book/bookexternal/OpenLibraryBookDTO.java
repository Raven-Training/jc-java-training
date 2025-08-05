package com.raven.training.presentation.dto.book.bookexternal;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.raven.training.util.deserializer.IdentifierDeserializer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object (DTO) that represents the response structure from the OpenLibrary API.
 * This class is designed to handle the dynamic JSON format of the OpenLibrary API,
 * where the key for the book data is the ISBN.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenLibraryBookDTO {
    private Map<String, BookData> books;

    /**
     * Custom setter method to handle the dynamic key for the book data.
     * This method is called by Jackson when a field not explicitly mapped
     * is found in the JSON.
     *
     * @param key The ISBN of the book.
     * @param value The {@link BookData} object containing the book's details.
     */
    @JsonAnySetter
    public void setBook(String key, BookData value) {
        this.books = Map.of(key, value);
    }

    /**
     * Represents the detailed data of a book within the OpenLibrary API response.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BookData {
        private String title;
        private String subtitle;
        private List<Publisher> publishers;
        @JsonProperty("publish_date")
        private String publishDate;
        @JsonProperty("number_of_pages")
        private Integer numberOfPages;
        private List<Author> authors;

        @JsonDeserialize(using = IdentifierDeserializer.class)
        private Map<String, Object> identifiers;
    }

    /**
     * Represents the publisher information for a book.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Publisher {
        private String name;
    }

    /**
     * Represents the author information for a book.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Author {
        private String name;
    }
}
