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

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {})
public interface IUserMapper {

    IUserMapper INSTANCE = Mappers.getMapper(IUserMapper.class);

    @Mapping(target = "books", ignore = true)
    User toEntity(UserRequest userRequest);

    @Mapping(target = "bookIds", expression = "java(mapBooksToBookIds(user.getBooks()))")
    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> userList);

    default List<UUID> mapBooksToBookIds(List<Book> books) {
        if (books == null) {
            return null;
        }
        return books.stream()
                .map(Book::getId)
                .collect(Collectors.toList());
    }
}
