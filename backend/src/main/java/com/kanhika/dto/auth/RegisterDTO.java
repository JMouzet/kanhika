package com.kanhika.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
        @NotBlank @Size(max=32) String username,
        @NotBlank @Size(max=255) String email,
        @NotBlank @Size(min=8, max=128) String password
) {}
