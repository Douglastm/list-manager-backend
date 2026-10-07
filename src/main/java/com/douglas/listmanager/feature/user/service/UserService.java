package com.douglas.listmanager.feature.user.service;

import com.douglas.listmanager.feature.user.dto.CreateUserRequest;
import com.douglas.listmanager.feature.user.dto.UpdateUserRequest;
import com.douglas.listmanager.feature.user.dto.UserResponse;
import com.douglas.listmanager.feature.user.entity.User;
import com.douglas.listmanager.feature.user.mapper.UserMapper;
import com.douglas.listmanager.feature.user.repository.UserRepository;
import com.douglas.listmanager.shared.exception.BusinessException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserResponse create(
            CreateUserRequest request
    ) {

        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(
                    "Email already registered"
            );
        }

        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(
                passwordEncoder.encode(request.password())
        );
        user.setActive(true);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new BusinessException(
                                "User not found"
                        )
                );

        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse update(
            UUID userId,
            UpdateUserRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new BusinessException(
                                "User not found"
                        )
                );

        if (
                !user.getEmail().equals(request.email()) &&
                        userRepository.existsByEmail(request.email())
        ) {
            throw new BusinessException(
                    "Email already registered"
            );
        }

        user.setName(request.name());
        user.setEmail(request.email());

        if (
                request.password() != null &&
                        !request.password().isBlank()
        ) {
            user.setPassword(
                    passwordEncoder.encode(
                            request.password()
                    )
            );
        }

        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public void delete(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new BusinessException(
                                "User not found"
                        )
                );

        user.setActive(false);

        userRepository.save(user);
    }
}