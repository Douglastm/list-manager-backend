package com.douglas.listmanager.feature.user.controller;

import com.douglas.listmanager.feature.user.dto.CreateUserRequest;
import com.douglas.listmanager.feature.user.dto.UpdateUserRequest;
import com.douglas.listmanager.feature.user.dto.UserResponse;
import com.douglas.listmanager.feature.user.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(
            @Valid @RequestBody CreateUserRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.create(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        return ResponseEntity.ok(
                userService.findById(userId)
        );
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> update(
            @Valid @RequestBody UpdateUserRequest request,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        return ResponseEntity.ok(
                userService.update(userId, request)
        );
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> delete(
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        userService.delete(userId);

        return ResponseEntity.noContent().build();
    }
}