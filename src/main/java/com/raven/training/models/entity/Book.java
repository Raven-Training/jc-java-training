package com.raven.training.models.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book")
public class Book {

    @Id
    UUID id;

    String gender;
    String author;
    String image;
    String title;
    String subtitle;
    String publisher;
    String year;
    Integer pages;
    String isbn;

    @PrePersist
    public void prePersist(){
        if(id == null){
            id = UUID.randomUUID();
        }
    }
}
