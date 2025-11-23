package com.arbre32.api.user.dto;

import java.util.UUID;

public record UserDto(
        UUID id,
        String handle,
        String email
) {}
