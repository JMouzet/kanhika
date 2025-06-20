package com.kanhika.dto.quiz;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record AnswerDTO(
        @NotNull @DecimalMin("0") @DecimalMax("3")
        Integer answerNo
) {}
