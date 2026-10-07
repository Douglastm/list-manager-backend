package com.douglas.listmanager.feature.user.service;

import com.douglas.listmanager.feature.user.dto.CreateUserRequest;
import com.douglas.listmanager.feature.user.dto.UpdateUserRequest;
import com.douglas.listmanager.feature.user.dto.UserResponse;
import com.douglas.listmanager.feature.user.entity.User;
import com.douglas.listmanager.feature.user.mapper.UserMapper;
import com.douglas.listmanager.feature.user.repository.UserRepository;
import com.douglas.listmanager.shared.exception.BusinessException;
import com.douglas.listmanager.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .name("Douglas")
                .email("douglas@email.com")
                .password("encoded-password")
                .active(true)
                .build();

        userResponse = new UserResponse(
                userId,
                "Douglas",
                "douglas@email.com",
                true,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    @Test
    void shouldCreateUserSuccessfully() {

        CreateUserRequest request = new CreateUserRequest(
                "Douglas",
                "douglas@email.com",
                "123456"
        );

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.password()))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse result = userService.create(request);

        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("Douglas", result.name());
        assertEquals("douglas@email.com", result.email());
        assertTrue(result.active());

        verify(userRepository).existsByEmail(request.email());
        verify(passwordEncoder).encode(request.password());
        verify(userRepository).save(any(User.class));
        verify(userMapper).toResponse(user);
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

        verify(userRepository).existsByEmail(request.email());

        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(anyString());
    }

    @Test
    void shouldFindUserById() {

        when(userRepository.findByIdAndActiveTrue(userId))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse result = userService.findById(userId);

        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("Douglas", result.name());

        verify(userRepository)
                .findByIdAndActiveTrue(userId);

        verify(userMapper)
                .toResponse(user);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findByIdAndActiveTrue(userId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.findById(userId)
        );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(userRepository)
                .findByIdAndActiveTrue(userId);

        verify(userMapper, never())
                .toResponse(any());
    }

    @Test
    void shouldFindAllActiveUsers() {

        User secondUser = User.builder()
                .id(UUID.randomUUID())
                .name("João")
                .email("joao@email.com")
                .password("encoded-password")
                .active(true)
                .build();

        UserResponse secondResponse = new UserResponse(
                secondUser.getId(),
                secondUser.getName(),
                secondUser.getEmail(),
                true,
                secondUser.getCreatedAt(),
                secondUser.getUpdatedAt()
        );

        when(userRepository.findAllByActiveTrue())
                .thenReturn(List.of(user, secondUser));

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        when(userMapper.toResponse(secondUser))
                .thenReturn(secondResponse);

        List<UserResponse> result = userService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("Douglas", result.get(0).name());
        assertEquals("João", result.get(1).name());

        verify(userRepository)
                .findAllByActiveTrue();

        verify(userMapper)
                .toResponse(user);

        verify(userMapper)
                .toResponse(secondUser);
    }

    @Test
    void shouldUpdateUserSuccessfully() {

        UpdateUserRequest request = new UpdateUserRequest(
                "Douglas Magalhães",
                "douglas.novo@email.com",
                "novaSenha123"
        );

        when(userRepository.findByIdAndActiveTrue(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmailAndIdNot(
                request.email(),
                userId
        )).thenReturn(false);

        when(passwordEncoder.encode(request.password()))
                .thenReturn("new-encoded-password");

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse result = userService.update(
                userId,
                request
        );

        assertNotNull(result);

        assertEquals(
                "Douglas Magalhães",
                user.getName()
        );

        assertEquals(
                "douglas.novo@email.com",
                user.getEmail()
        );

        assertEquals(
                "new-encoded-password",
                user.getPassword()
        );

        verify(userRepository)
                .findByIdAndActiveTrue(userId);

        verify(userRepository)
                .existsByEmailAndIdNot(
                        request.email(),
                        userId
                );

        verify(passwordEncoder)
                .encode(request.password());

        verify(userRepository)
                .save(user);
    }

    @Test
    void shouldUpdateUserWithoutChangingPassword() {

        String oldPassword = user.getPassword();

        UpdateUserRequest request = new UpdateUserRequest(
                "Douglas Magalhães",
                "douglas.novo@email.com",
                null
        );

        when(userRepository.findByIdAndActiveTrue(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmailAndIdNot(
                request.email(),
                userId
        )).thenReturn(false);

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        userService.update(userId, request);

        assertEquals(
                oldPassword,
                user.getPassword()
        );

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository)
                .save(user);
    }

    @Test
    void shouldNotUpdateUserWhenEmailAlreadyExists() {

        UpdateUserRequest request = new UpdateUserRequest(
                "Douglas",
                "existing@email.com",
                null
        );

        when(userRepository.findByIdAndActiveTrue(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmailAndIdNot(
                request.email(),
                userId
        )).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.update(userId, request)
        );

        assertEquals(
                "Email already registered",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void shouldDeactivateUserSuccessfully() {

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        userService.deactivate(userId);

        assertFalse(user.getActive());

        verify(userRepository)
                .findById(userId);

        verify(userRepository)
                .save(user);
    }

    @Test
    void shouldNotDeactivateUserWhenAlreadyInactive() {

        user.setActive(false);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.deactivate(userId)
        );

        assertEquals(
                "User is already inactive",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void shouldNotDeactivateUserWhenNotFound() {

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.deactivate(userId)
        );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }
}