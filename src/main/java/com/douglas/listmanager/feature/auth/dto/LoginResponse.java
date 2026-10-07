package com.douglas.listmanager.feature.auth.dto;

public record LoginResponse(

        String accessToken,

        String tokenType,

        long expiresIn

) {
}