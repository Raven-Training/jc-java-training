package com.raven.training.service.implementation;

import com.raven.training.exception.error.BookAlreadyInCollectionException;
import com.raven.training.exception.error.BookNotInCollectionException;
import com.raven.training.exception.error.BookNotFoundException;
import com.raven.training.exception.error.UserNotFoundException;
import com.raven.training.mapper.IUserMapper;
import com.raven.training.persistence.entity.Book;
import com.raven.training.persistence.entity.User;
import com.raven.training.persistence.repository.IUserRepository;
import com.raven.training.persistence.repository.IBookRepository;
import com.raven.training.presentation.dto.user.UserRequest;
import com.raven.training.presentation.dto.user.UserResponse;
import com.raven.training.service.interfaces.IUserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final IUserMapper userMapper;
    private final IBookRepository bookRepository;

    @Override
    public Page<UserResponse> findAll(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);

        return users.map(userMapper::toResponse);
    }

    @Override
    @Transactional
    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse update(UUID id, UserRequest userRequest) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    if (userRequest.userName() != null && !userRequest.userName().trim().isEmpty()){
                        existingUser.setUserName(userRequest.userName());
                    }
                    if (userRequest.name() != null && !userRequest.name().trim().isEmpty()){
                        existingUser.setName(userRequest.name());
                    }
                    if (userRequest.birthDate() != null){
                        existingUser.setBirthDate(userRequest.birthDate());
                    }
                    if (userRequest.bookIds() != null && !userRequest.bookIds().isEmpty()) {

                        existingUser.getBooks().clear();

                        for (UUID bookId : userRequest.bookIds()) {
                            bookRepository.findById(bookId).ifPresent(book ->
                                existingUser.getBooks().add(book)
                            );
                        }
                    }

                    User userUpdate = userRepository.save(existingUser);
                    return userMapper.toResponse(userUpdate);
                })
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserResponse addBookToUser(UUID userId, UUID bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID not found: " + userId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with ID not found: " + bookId));

        boolean bookExists = user.getBooks().stream()
                .anyMatch(b -> b.getId().equals(bookId));
                
        if (bookExists) {
            throw new BookAlreadyInCollectionException(userId, bookId);
        }

        user.getBooks().add(book);
        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }
    
    @Override
    @Transactional
    public UserResponse removeBookFromUser(UUID userId, UUID bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID not found: " + userId));

        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Book with ID not found: " + bookId);
        }

        if (user.getBooks() == null || user.getBooks().isEmpty()) {
            throw new BookNotInCollectionException(userId, bookId);
        }

        boolean removed = user.getBooks().removeIf(book -> book.getId().equals(bookId));

        if (!removed) {
            throw new BookNotInCollectionException(userId, bookId);
        }

        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findUserByUserName(username)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
}
