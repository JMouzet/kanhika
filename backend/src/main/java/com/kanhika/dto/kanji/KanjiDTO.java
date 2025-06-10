package com.kanhika.dto.kanji;

import java.util.List;

public record KanjiDTO(
        String kanji,
        int grade,
        int jlpt,
        int strokes,
        List<String> meanings,
        List<String> kunReadings,
        List<String> onReadings
) {}
