package com.douglas.listmanager.feature.list.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ListResponse(
        UUID id,
        String name,
        String description,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}