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
import jakarta.transaction.Transactional;
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

@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private IUserRepository userRepository;
    private IUserMapper userMapper;
    private IAuthUserRepository authUserRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtils jwtUtils;


    @Override
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

    public AuthLoginResponse loginUser(AuthLoginRequest authLoginRequest) {

        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(username, password);

        String accessToken = this.jwtUtils.createToken(authentication);

        return new AuthLoginResponse(username, "User logged in correctly", accessToken, true);
    }

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
