package com.kanhika.dto.comment;

import java.time.LocalDateTime;

public record CommentDTO(
                int id,
                String username,
                String message,
                int vote,
                int userVote,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
}