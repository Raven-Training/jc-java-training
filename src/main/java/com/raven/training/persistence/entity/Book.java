package com.raven.training.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

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

    @ManyToMany(mappedBy = "books", fetch = FetchType.LAZY)
    @Builder.Default
    private List<User> users = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
