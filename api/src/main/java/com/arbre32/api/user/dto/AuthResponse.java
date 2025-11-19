package com.arbre32.api.user.dto;

public record AuthResponse(
        String token,
        UserDto user
) {}
