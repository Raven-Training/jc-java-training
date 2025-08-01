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

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenLibraryBookDTO {
    private Map<String, BookData> books;

    @JsonAnySetter
    public void setBook(String key, BookData value) {
        this.books = Map.of(key, value);
    }

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

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Publisher {
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Author {
        private String name;
    }
}
