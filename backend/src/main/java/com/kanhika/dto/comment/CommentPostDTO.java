package com.kanhika.dto.comment;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public record CommentPostDTO(
    @NotBlank
    @Column(length = 2000)
    String message
) {}
