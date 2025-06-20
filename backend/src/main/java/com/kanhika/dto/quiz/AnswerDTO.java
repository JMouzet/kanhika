package com.kanhika.dto.quiz;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record AnswerDTO(
        @NotBlank @DecimalMin("0") @DecimalMax("3")
        int answerNo
) {}
