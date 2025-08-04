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

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthUserController {

    private UserDetailServiceImpl userDetailService;

    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponse> login(@RequestBody @Valid AuthLoginRequest authLoginRequest){
        AuthLoginResponse authLoginResponse = userDetailService.loginUser(authLoginRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(authLoginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthRegisterResponse> register(@RequestBody @Valid AuthRegisterRequest authRegisterRequest){
        AuthRegisterResponse authRegisterResponse = userDetailService.registerUser(authRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(authRegisterResponse);
    }
}
