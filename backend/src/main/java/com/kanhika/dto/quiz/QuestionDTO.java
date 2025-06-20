package com.kanhika.dto.quiz;

public record QuestionDTO(
        int id,
        String question,
        String answer0,
        String answer1,
        String answer2,
        String answer3
) {}
