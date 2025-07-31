package com.raven.training.presentation.dto.book.bookexternal;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDTO {
    private String isbn;
    private String title;
    private String subtitle;
    private List<String> publishers;
    private String publishDate;
    private Integer numberOfPages;
    private List<String> authors;
}
