package com.kanhika.dto.quiz;

public record ResultDTO(
        boolean answerIs,
        String rightAnswer,
        int scored,
        int outOf,
        boolean endOfQuiz
) {}
