package com.kanhika.dto.quiz;

public record ResultDTO(
        boolean answerIs,
        String rightAnswer,
        boolean continues
) {}
