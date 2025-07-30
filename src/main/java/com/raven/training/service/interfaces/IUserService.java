package com.raven.training.service.interfaces;

import com.raven.training.presentation.dto.user.UserRequest;
import com.raven.training.presentation.dto.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IUserService {

    Page<UserResponse> findAll(Pageable pageable);
    UserResponse findById(UUID id);
    UserResponse save(UserRequest userRequest);
    UserResponse update(UUID id, UserRequest userRequest);
    void delete(UUID id);
    UserResponse addBookToUser(UUID userId, UUID bookId);
    UserResponse removeBookFromUser(UUID userId, UUID bookId);
}
