package com.kanhika.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUsernameDTO(
        @NotBlank @Size(max=32) String username
) {}
