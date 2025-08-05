package com.raven.training.presentation.controller;

import com.raven.training.presentation.dto.login.AuthLoginRequest;
import com.raven.training.presentation.dto.login.AuthLoginResponse;
import com.raven.training.presentation.dto.register.AuthRegisterRequest;
import com.raven.training.presentation.dto.register.AuthRegisterResponse;
import com.raven.training.service.implementation.UserDetailServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling user authentication and registration.
 * This class exposes REST endpoints for user login and registration processes.
 *
 * @author Juan Esteban Camacho Barrera
 * @version 1.0
 * @since 2025-08-05
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthUserController {

    private UserDetailServiceImpl userDetailService;

    /**
     * Endpoint for user login.
     * This method receives login credentials, authenticates the user, and returns
     * an access token upon successful login.
     *
     * @param authLoginRequest The {@link AuthLoginRequest} containing the user's login credentials.
     * @return A {@link ResponseEntity} with an {@link AuthLoginResponse} and an HTTP status of 201 (Created).
     */
    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponse> login(@RequestBody @Valid AuthLoginRequest authLoginRequest){
        AuthLoginResponse authLoginResponse = userDetailService.loginUser(authLoginRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(authLoginResponse);
    }

    /**
     * Endpoint for new user registration.
     * This method receives user registration data, validates it, creates a new user
     * in the system, and returns a confirmation response.
     *
     * @param authRegisterRequest The {@link AuthRegisterRequest} containing the new user's data.
     * @return A {@link ResponseEntity} with an {@link AuthRegisterResponse} and an HTTP status of 201 (Created).
     */
    @PostMapping("/register")
    public ResponseEntity<AuthRegisterResponse> register(@RequestBody @Valid AuthRegisterRequest authRegisterRequest){
        AuthRegisterResponse authRegisterResponse = userDetailService.registerUser(authRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(authRegisterResponse);
    }
}
