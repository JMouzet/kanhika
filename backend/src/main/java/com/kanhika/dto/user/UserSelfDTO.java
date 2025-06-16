package com.kanhika.dto.user;

import java.time.LocalDateTime;

public record UserSelfDTO(
        String username,
        String bio,
        String email,
        int exp,
        int flame,
        String role,
        LocalDateTime createdAt
) {}