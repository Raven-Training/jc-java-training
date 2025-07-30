package com.raven.training.service.implementation;

import com.raven.training.persistence.entity.AuthUser;
import com.raven.training.persistence.repository.IAuthUserRepository;
import com.raven.training.presentation.dto.login.AuthLoginRequest;
import com.raven.training.presentation.dto.login.AuthLoginResponse;
import com.raven.training.presentation.dto.register.AuthRegisterRequest;
import com.raven.training.presentation.dto.register.AuthRegisterResponse;
import com.raven.training.util.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

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

    public AuthRegisterResponse registerUser(AuthRegisterRequest authregisterRequest){
        String username = authregisterRequest.username();
        String password = authregisterRequest.password();
        String email = authregisterRequest.email();

        String encodedPassword = passwordEncoder.encode(password);

        AuthUser user = AuthUser.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .isEnabled(true)
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialNoExpired(true)
                .build();

        user.setId(UUID.randomUUID());

        AuthUser userEntitySaved = this.authUserRepository.save(user);

        return new AuthRegisterResponse(userEntitySaved.getUsername(),userEntitySaved.getEmail(),"User created successfully",true);
    }
}
