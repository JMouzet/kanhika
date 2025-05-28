package com.kanhika.dto.auth;

public record RegisterDTO(
        String username,
        String email,
        String password
) {}
