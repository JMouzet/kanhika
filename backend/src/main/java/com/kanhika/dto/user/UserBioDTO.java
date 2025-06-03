package com.kanhika.dto.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserBioDTO(
        @NotNull @Size(max=200) String bio
) {}
