package com.raven.training.mapper;

import com.raven.training.persistence.entity.Book;
import com.raven.training.presentation.dto.book.BookRequest;
import com.raven.training.presentation.dto.book.BookResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * Mapper interface for converting between {@link Book} entities, {@link BookRequest} DTOs,
 * and {@link BookResponse} DTOs.
 * This interface uses MapStruct to provide the implementation, ensuring
 * type-safe and efficient object mapping.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IBookMapper {

    /**
     * Converts a {@link BookRequest} DTO to a {@link Book} entity.
     * The 'id' and 'users' fields are ignored during this mapping, as they
     * are typically managed by the persistence layer.
     *
     * @param bookRequest The DTO containing the book data.
     * @return A new {@link Book} entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    Book toEntity(BookRequest bookRequest);

    /**
     * Converts a {@link Book} entity to a {@link BookResponse} DTO.
     *
     * @param book The entity to convert.
     * @return A {@link BookResponse} DTO.
     */
    BookResponse toResponse(Book book);

    /**
     * Converts a list of {@link Book} entities to a list of {@link BookResponse} DTOs.
     *
     * @param bookList The list of entities to convert.
     * @return A list of {@link BookResponse} DTOs.
     */
    List<BookResponse> toResponseList(List<Book> bookList);
}
