package com.kanhika.dto.quiz;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuizInDTO(
        @NotBlank
        String questions_type,

        @NotNull
        @DecimalMin("5")
        @DecimalMax("30")
        Integer questions_number,

        @NotBlank
        String difficulty_type,

        @NotNull
        Integer difficulty_number
) {}
