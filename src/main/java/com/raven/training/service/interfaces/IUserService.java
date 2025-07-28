package com.raven.training.service.interfaces;

import com.raven.training.presentation.dto.user.UserRequest;
import com.raven.training.presentation.dto.user.UserResponse;

import java.util.List;
import java.util.UUID;

public interface IUserService {

    List<UserResponse> findAll();
    UserResponse findById(UUID id);
    UserResponse save(UserRequest userRequest);
    UserResponse update(UUID id, UserRequest userRequest);
    void delete(UUID id);
    UserResponse addBookToUser(UUID userId, UUID bookId);
    UserResponse removeBookFromUser(UUID userId, UUID bookId);
}
