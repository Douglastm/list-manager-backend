package com.douglas.listmanager.feature.list.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateListRequest(

        @NotBlank(message = "Name is required")
        @Size(
                max = 100,
                message = "Name must have at most 100 characters"
        )
        String name,

        @Size(
                max = 500,
                message = "Description must have at most 500 characters"
        )
        String description
) {
}