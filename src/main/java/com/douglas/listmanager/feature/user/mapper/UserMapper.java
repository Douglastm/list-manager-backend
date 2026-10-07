package com.douglas.listmanager.feature.user.mapper;

import com.douglas.listmanager.feature.user.dto.UserResponse;
import com.douglas.listmanager.feature.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}