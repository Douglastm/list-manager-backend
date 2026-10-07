package com.douglas.listmanager.feature.user.service;

import com.douglas.listmanager.feature.user.dto.CreateUserRequest;
import com.douglas.listmanager.feature.user.dto.UpdateUserRequest;
import com.douglas.listmanager.feature.user.dto.UserResponse;
import com.douglas.listmanager.feature.user.entity.User;
import com.douglas.listmanager.feature.user.mapper.UserMapper;
import com.douglas.listmanager.feature.user.repository.UserRepository;
import com.douglas.listmanager.shared.exception.BusinessException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUser() {

        CreateUserRequest request = new CreateUserRequest(
                "Douglas",
                "douglas@email.com",
                "123456"
        );

        User user = new User();

        UserResponse response = new UserResponse(
                UUID.randomUUID(),
                "Douglas",
                "douglas@email.com",
                true,
                null,
                null
        );

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.password()))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        UserResponse result = userService.create(request);

        assertSame(response, result);

        verify(userRepository)
                .existsByEmail(request.email());

        verify(passwordEncoder)
                .encode(request.password());

        verify(userRepository)
                .save(any(User.class));

        verify(userMapper)
                .toResponse(user);
    }

    @Test
    void shouldNotCreateUserWhenEmailAlreadyExists() {

        CreateUserRequest request = new CreateUserRequest(
                "Douglas",
                "douglas@email.com",
                "123456"
        );

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.create(request)
        );

        assertEquals(
                "Email already registered",
                exception.getMessage()
        );

        verify(userRepository)
                .existsByEmail(request.email());

        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(any());
    }

    @Test
    void shouldFindUserById() {

        UUID userId = UUID.randomUUID();

        User user = new User();

        UserResponse response = new UserResponse(
                userId,
                "Douglas",
                "douglas@email.com",
                true,
                null,
                null
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(response);

        UserResponse result = userService.findById(userId);

        assertSame(response, result);

        verify(userRepository)
                .findById(userId);

        verify(userMapper)
                .toResponse(user);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotFound() {

        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.findById(userId)
        );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(userRepository)
                .findById(userId);

        verify(userMapper, never())
                .toResponse(any(User.class));
    }

    @Test
    void shouldUpdateUser() {

        UUID userId = UUID.randomUUID();

        UpdateUserRequest request = new UpdateUserRequest(
                "Douglas Alterado",
                "douglas.novo@email.com",
                "654321"
        );

        User user = new User();

        user.setName("Douglas");
        user.setEmail("douglas@email.com");
        user.setPassword("old-password");
        user.setActive(true);

        UserResponse response = new UserResponse(
                userId,
                "Douglas Alterado",
                "douglas.novo@email.com",
                true,
                null,
                null
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.password()))
                .thenReturn("new-password");

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        UserResponse result = userService.update(
                userId,
                request
        );

        assertSame(response, result);

        assertEquals(
                "Douglas Alterado",
                user.getName()
        );

        assertEquals(
                "douglas.novo@email.com",
                user.getEmail()
        );

        assertEquals(
                "new-password",
                user.getPassword()
        );

        verify(userRepository)
                .findById(userId);

        verify(userRepository)
                .existsByEmail(request.email());

        verify(passwordEncoder)
                .encode(request.password());

        verify(userRepository)
                .save(user);

        verify(userMapper)
                .toResponse(user);
    }

    @Test
    void shouldUpdateUserWithoutChangingPassword() {

        UUID userId = UUID.randomUUID();

        UpdateUserRequest request = new UpdateUserRequest(
                "Douglas Alterado",
                "douglas.novo@email.com",
                null
        );

        User user = new User();

        user.setName("Douglas");
        user.setEmail("douglas@email.com");
        user.setPassword("old-password");
        user.setActive(true);

        UserResponse response = new UserResponse(
                userId,
                "Douglas Alterado",
                "douglas.novo@email.com",
                true,
                null,
                null
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        UserResponse result = userService.update(
                userId,
                request
        );

        assertSame(response, result);

        assertEquals(
                "old-password",
                user.getPassword()
        );

        verify(passwordEncoder, never())
                .encode(any());

        verify(userRepository)
                .save(user);
    }

    @Test
    void shouldNotUpdateUserWhenEmailAlreadyExists() {

        UUID userId = UUID.randomUUID();

        UpdateUserRequest request = new UpdateUserRequest(
                "Douglas",
                "outro@email.com",
                null
        );

        User user = new User();

        user.setName("Douglas");
        user.setEmail("douglas@email.com");
        user.setPassword("old-password");
        user.setActive(true);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.update(
                        userId,
                        request
                )
        );

        assertEquals(
                "Email already registered",
                exception.getMessage()
        );

        verify(userRepository)
                .findById(userId);

        verify(userRepository)
                .existsByEmail(request.email());

        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(any());
    }

    @Test
    void shouldUpdateUserWithSameEmail() {

        UUID userId = UUID.randomUUID();

        UpdateUserRequest request = new UpdateUserRequest(
                "Douglas Alterado",
                "douglas@email.com",
                null
        );

        User user = new User();

        user.setName("Douglas");
        user.setEmail("douglas@email.com");
        user.setPassword("old-password");
        user.setActive(true);

        UserResponse response = new UserResponse(
                userId,
                "Douglas Alterado",
                "douglas@email.com",
                true,
                null,
                null
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        UserResponse result = userService.update(
                userId,
                request
        );

        assertSame(response, result);

        assertEquals(
                "Douglas Alterado",
                user.getName()
        );

        assertEquals(
                "douglas@email.com",
                user.getEmail()
        );

        verify(userRepository)
                .findById(userId);

        verify(userRepository, never())
                .existsByEmail(request.email());

        verify(userRepository)
                .save(user);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonexistentUser() {

        UUID userId = UUID.randomUUID();

        UpdateUserRequest request = new UpdateUserRequest(
                "Douglas",
                "douglas@email.com",
                null
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.update(
                        userId,
                        request
                )
        );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(userRepository)
                .findById(userId);

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void shouldDeleteUser() {

        UUID userId = UUID.randomUUID();

        User user = new User();

        user.setName("Douglas");
        user.setEmail("douglas@email.com");
        user.setPassword("password");
        user.setActive(true);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        userService.delete(userId);

        assertFalse(user.getActive());

        verify(userRepository)
                .findById(userId);

        verify(userRepository)
                .save(user);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonexistentUser() {

        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.delete(userId)
        );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(userRepository)
                .findById(userId);

        verify(userRepository, never())
                .save(any(User.class));
    }
}