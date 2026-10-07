package com.douglas.listmanager.feature.item.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ItemResponse(
        UUID id,
        UUID listId,
        String name,
        String description,
        Boolean completed,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}