package com.raven.training.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Entity representing a user of the application.
 * This class maps to the "users" table in the database and contains
 * information about the user, including their personal details and a
 * collection of books they own or have added.
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
@Table(name = "users")
public class User {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    private String userName;

    private String name;

    private LocalDate birthDate;

    /**
     * A list of books associated with the user.
     * This is a many-to-many relationship, and the relationship is managed
     * by the join table "user_books".
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_books",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    @Builder.Default
    private List<Book> books = new ArrayList<>();

    /**
     * Callback method executed before the entity is persisted.
     * It ensures a UUID is generated for the user if one is not already set.
     */
    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}