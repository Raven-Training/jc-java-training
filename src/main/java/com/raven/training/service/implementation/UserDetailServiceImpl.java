package com.raven.training.service.implementation;

import com.raven.training.exception.error.EmailAlreadyExistsException;
import com.raven.training.exception.error.UsernameAlreadyExistsException;
import com.raven.training.mapper.IUserMapper;
import com.raven.training.persistence.entity.AuthUser;
import com.raven.training.persistence.entity.User;
import com.raven.training.persistence.repository.IAuthUserRepository;
import com.raven.training.persistence.repository.IUserRepository;
import com.raven.training.presentation.dto.login.AuthLoginRequest;
import com.raven.training.presentation.dto.login.AuthLoginResponse;
import com.raven.training.presentation.dto.register.AuthRegisterRequest;
import com.raven.training.presentation.dto.register.AuthRegisterResponse;
import com.raven.training.util.JwtUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Service implementation for user authentication and authorization using Spring Security.
 * This class handles loading user details, authentication, login, and registration processes.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private IUserRepository userRepository;
    private IUserMapper userMapper;
    private IAuthUserRepository authUserRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtils jwtUtils;

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * is delegated to the repository. This method is crucial for Spring Security
     * to perform the authentication process.
     *
     * @param username The username of the user to load.
     * @return A fully populated user record (never <code>null</code>).
     * @throws UsernameNotFoundException if the user could not be found.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUser user = authUserRepository.findAuthUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("The user " + username + " doesn't exist"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                user.isAccountNoExpired(),
                user.isCredentialNoExpired(),
                user.isAccountNoLocked(),
                new ArrayList<>()
        );
    }

    /**
     * Authenticates a user with a given username and password.
     * It uses the <code>loadUserByUsername</code> method to retrieve user details and then
     * verifies the provided password.
     *
     * @param username The username of the user.
     * @param password The raw password submitted by the user.
     * @return The authenticated {@link Authentication} object.
     * @throws BadCredentialsException if the password does not match the stored password.
     */
    public Authentication authenticate(String username, String password) {
        UserDetails userDetails = this.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities());
    }

    /**
     * Handles the user login process by authenticating the user and generating a JWT access token.
     *
     * @param authLoginRequest The {@link AuthLoginRequest} containing login credentials.
     * @return An {@link AuthLoginResponse} with the username and the JWT token.
     */
    public AuthLoginResponse loginUser(AuthLoginRequest authLoginRequest) {

        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(username, password);

        String accessToken = this.jwtUtils.createToken(authentication);

        return new AuthLoginResponse(username, "User logged in correctly", accessToken, true);
    }

    /**
     * Registers a new user, saving the authentication details and the user profile.
     * This method is transactional, ensuring that both records (AuthUser and User) are
     * created atomically.
     *
     * @param authRegisterRequest The {@link AuthRegisterRequest} with the user registration data.
     * @return An {@link AuthRegisterResponse} with a confirmation message.
     * @throws UsernameAlreadyExistsException if the username is already in use.
     * @throws EmailAlreadyExistsException if the email is already in use.
     */
    @Transactional
    public AuthRegisterResponse registerUser(AuthRegisterRequest authRegisterRequest) {
        String username = authRegisterRequest.username();
        String password = authRegisterRequest.password();
        String email = authRegisterRequest.email();
        String name = authRegisterRequest.name();
        LocalDate birthDate = authRegisterRequest.birthDate();

        if (authUserRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("The username is already in use");
        }

        if (authUserRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email is already in use");
        }

        String encodedPassword = passwordEncoder.encode(password);
        UUID userId = UUID.randomUUID();

        AuthUser authUser = AuthUser.builder()
                .id(userId)
                .username(username)
                .password(encodedPassword)
                .email(email)
                .isEnabled(true)
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialNoExpired(true)
                .build();

        AuthUser savedAuthUser = authUserRepository.save(authUser);

        User user = User.builder()
                .id(userId)
                .userName(username)
                .name(name)
                .birthDate(birthDate)
                .books(new ArrayList<>())
                .build();

        userRepository.save(user);

        return new AuthRegisterResponse(
                savedAuthUser.getUsername(),
                savedAuthUser.getEmail(),
                "User successfully registered",
                true
        );
    }
}
