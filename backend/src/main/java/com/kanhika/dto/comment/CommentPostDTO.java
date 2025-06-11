package com.kanhika.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentPostDTO(
    @NotBlank
    @Size(max = 2000)
    String message
) {}
