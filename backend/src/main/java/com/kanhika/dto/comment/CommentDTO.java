package com.kanhika.dto.comment;

import java.time.LocalDateTime;

public record CommentDTO(
        int id,
        String username,
        String message,
        int vote,
        boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}