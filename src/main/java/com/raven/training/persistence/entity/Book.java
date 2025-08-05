package com.raven.training.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

/**
 * Entity representing a book.
 * This class maps to the "book" table in the database and contains
 * all the relevant information about a book, including its relationship
 * with users who may have it in their collections.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Table(name = "book")
public class Book {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    private String gender;
    private String author;
    private String image;
    private String title;
    private String subtitle;
    private String publisher;
    private String year;
    private Integer pages;
    private String isbn;

    /**
     * A list of users who have this book in their collection.
     * This is a many-to-many relationship mapped by the "books" field in the User entity.
     */
    @ManyToMany(mappedBy = "books", fetch = FetchType.LAZY)
    @Builder.Default
    private List<User> users = new ArrayList<>();

    /**
     * Callback method executed before the entity is persisted.
     * It ensures a UUID is generated if one is not already set.
     */
    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
