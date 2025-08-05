package com.raven.training.presentation.dto.book.bookexternal;

import lombok.*;

import java.util.List;

/**
 * Data Transfer Object (DTO) for book response data.
 * This class encapsulates the book information to be transferred
 * between the application layers and sent as a response to the client.
 *
 * It includes details such as ISBN, title, authors, and publication information.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
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
