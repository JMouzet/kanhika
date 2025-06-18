package com.kanhika.dto.kanji;

import java.util.List;

public record KanjiSearchDTO(
        int found,
        int page,
        int total,
        int totalPage,
        List<KanjiDTO> kanjis
) {}
