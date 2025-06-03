package com.kanhika.dto.user;

import java.time.LocalDateTime;

public record UserPublicDTO(
        String username,
        String bio,
        int exp,
        int flame,
        String role,
        LocalDateTime createdAt
) {}