package com.douglas.listmanager.feature.auth.service;

import com.douglas.listmanager.feature.auth.dto.LoginRequest;
import com.douglas.listmanager.feature.auth.dto.LoginResponse;
import com.douglas.listmanager.feature.user.entity.User;
import com.douglas.listmanager.feature.user.repository.UserRepository;
import com.douglas.listmanager.shared.exception.BusinessException;
import com.douglas.listmanager.shared.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() ->
                        new BusinessException(
                                "Invalid email or password"
                        )
                );

        if (!user.getActive()) {
            throw new BusinessException(
                    "User is inactive"
            );
        }

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {
            throw new BusinessException(
                    "Invalid email or password"
            );
        }

        UUID userId = user.getId();

        String token = jwtService.generateToken(userId);

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpiration()
        );
    }
}