package com.raven.training.mapper;

import com.raven.training.persistence.entity.Book;
import com.raven.training.persistence.entity.User;
import com.raven.training.presentation.dto.user.UserRequest;
import com.raven.training.presentation.dto.user.UserResponse;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between {@link User} entities, {@link UserRequest} DTOs,
 * and {@link UserResponse} DTOs.
 * This interface uses MapStruct to provide the implementation, ensuring
 * type-safe and efficient object mapping.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {})
public interface IUserMapper {

    IUserMapper INSTANCE = Mappers.getMapper(IUserMapper.class);

    /**
     * Converts a {@link UserRequest} DTO to a {@link User} entity.
     * The 'books' field is ignored during this mapping, as it
     * is typically managed by the service layer.
     *
     * @param userRequest The DTO containing the user data.
     * @return A new {@link User} entity.
     */
    @Mapping(target = "books", ignore = true)
    User toEntity(UserRequest userRequest);

    /**
     * Converts a {@link User} entity to a {@link UserResponse} DTO.
     * The list of {@link Book} entities is converted to a list of book UUIDs.
     *
     * @param user The entity to convert.
     * @return A {@link UserResponse} DTO.
     */
    @Mapping(target = "bookIds", expression = "java(mapBooksToBookIds(user.getBooks()))")
    UserResponse toResponse(User user);

    /**
     * Converts a list of {@link User} entities to a list of {@link UserResponse} DTOs.
     *
     * @param userList The list of entities to convert.
     * @return A list of {@link UserResponse} DTOs.
     */
    List<UserResponse> toResponseList(List<User> userList);

    /**
     * Helper method to map a list of {@link Book} entities to a list of their UUIDs.
     *
     * @param books The list of book entities.
     * @return A list of book UUIDs.
     */
    default List<UUID> mapBooksToBookIds(List<Book> books) {
        if (books == null) {
            return null;
        }
        return books.stream()
                .map(Book::getId)
                .collect(Collectors.toList());
    }
}
