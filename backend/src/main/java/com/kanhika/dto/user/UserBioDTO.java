package com.kanhika.dto.user;

import jakarta.validation.constraints.Size;

public record UserBioDTO(
        @Size(max=200) String bio
) {}
