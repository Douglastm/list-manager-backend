package com.douglas.listmanager.feature.auth.controller;

import com.douglas.listmanager.feature.auth.dto.LoginRequest;
import com.douglas.listmanager.feature.auth.dto.LoginResponse;
import com.douglas.listmanager.feature.auth.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }
}