package com.douglas.listmanager.feature.user.controller;

import com.douglas.listmanager.feature.user.dto.CreateUserRequest;
import com.douglas.listmanager.feature.user.dto.UpdateUserRequest;
import com.douglas.listmanager.feature.user.dto.UserResponse;
import com.douglas.listmanager.feature.user.service.UserService;

import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    @Test
    void shouldCreateUser() {

        UUID userId = UUID.randomUUID();

        CreateUserRequest request = new CreateUserRequest(
                "Douglas",
                "douglas@email.com",
                "123456"
        );

        UserResponse response = new UserResponse(
                userId,
                "Douglas",
                "douglas@email.com",
                true,
                null,
                null
        );

        when(userService.create(request))
                .thenReturn(response);

        ResponseEntity<UserResponse> result =
                userController.create(request);

        assertEquals(
                HttpStatus.CREATED,
                result.getStatusCode()
        );

        assertEquals(
                response,
                result.getBody()
        );

        verify(userService)
                .create(request);
    }

    @Test
    void shouldReturnAuthenticatedUser() {

        UUID userId = UUID.randomUUID();

        UserResponse response = new UserResponse(
                userId,
                "Douglas",
                "douglas@email.com",
                true,
                null,
                null
        );

        when(authentication.getPrincipal())
                .thenReturn(userId);

        when(userService.findById(userId))
                .thenReturn(response);

        ResponseEntity<UserResponse> result =
                userController.me(authentication);

        assertEquals(
                HttpStatus.OK,
                result.getStatusCode()
        );

        assertEquals(
                response,
                result.getBody()
        );

        verify(authentication)
                .getPrincipal();

        verify(userService)
                .findById(userId);
    }

    @Test
    void shouldUpdateAuthenticatedUser() {

        UUID userId = UUID.randomUUID();

        UpdateUserRequest request = new UpdateUserRequest(
                "Douglas Alterado",
                "douglas@email.com",
                "123456"
        );

        UserResponse response = new UserResponse(
                userId,
                "Douglas Alterado",
                "douglas@email.com",
                true,
                null,
                null
        );

        when(authentication.getPrincipal())
                .thenReturn(userId);

        when(
                userService.update(
                        eq(userId),
                        eq(request)
                )
        ).thenReturn(response);

        ResponseEntity<UserResponse> result =
                userController.update(
                        request,
                        authentication
                );

        assertEquals(
                HttpStatus.OK,
                result.getStatusCode()
        );

        assertEquals(
                response,
                result.getBody()
        );

        verify(authentication)
                .getPrincipal();

        verify(userService)
                .update(
                        userId,
                        request
                );
    }

    @Test
    void shouldDeleteAuthenticatedUser() {

        UUID userId = UUID.randomUUID();

        when(authentication.getPrincipal())
                .thenReturn(userId);

        ResponseEntity<Void> result =
                userController.delete(authentication);

        assertEquals(
                HttpStatus.NO_CONTENT,
                result.getStatusCode()
        );

        verify(authentication)
                .getPrincipal();

        verify(userService)
                .delete(userId);
    }
}