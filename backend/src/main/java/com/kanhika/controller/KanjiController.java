package com.kanhika.controller;

import com.kanhika.dto.kanji.KanjiDTO;
import com.kanhika.service.KanjiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kanjis")
public class KanjiController {

    private final KanjiService kanjiService;

    public KanjiController(KanjiService kanjiService) {
        this.kanjiService = kanjiService;
    }

    @GetMapping("/{kanji}")
    public ResponseEntity<KanjiDTO> getKanji(@PathVariable String kanji) {
        return ResponseEntity.ok(kanjiService.getKanji(kanji));
    }

}
