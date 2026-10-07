package com.douglas.listmanager.feature.user.service;

import com.douglas.listmanager.feature.user.dto.CreateUserRequest;
import com.douglas.listmanager.feature.user.dto.UpdateUserRequest;
import com.douglas.listmanager.feature.user.dto.UserResponse;
import com.douglas.listmanager.feature.user.entity.User;
import com.douglas.listmanager.feature.user.mapper.UserMapper;
import com.douglas.listmanager.feature.user.repository.UserRepository;
import com.douglas.listmanager.shared.exception.BusinessException;
import com.douglas.listmanager.shared.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already registered");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public UserResponse update(
            UUID id,
            UpdateUserRequest request
    ) {

        User user = userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        if (userRepository.existsByEmailAndIdNot(
                request.email(),
                id
        )) {
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
                    passwordEncoder.encode(request.password())
            );
        }

        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {

        User user = userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {

        return userRepository.findAllByActiveTrue()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional
    public void deactivate(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        if (!user.getActive()) {
            throw new BusinessException(
                    "User is already inactive"
            );
        }

        user.setActive(false);

        userRepository.save(user);
    }
}