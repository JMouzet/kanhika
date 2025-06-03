package com.kanhika.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserEmailDTO(
        @NotBlank @Size(max=255) String email
) {}
