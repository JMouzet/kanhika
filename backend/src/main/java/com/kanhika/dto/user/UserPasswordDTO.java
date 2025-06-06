package com.kanhika.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserPasswordDTO(
        @NotNull String oldPassword,
        @NotBlank @Size(min=8, max=128) String newPassword
) {}
