package com.kanhika.controller;

import com.kanhika.dto.kanji.KanjiDTO;
import com.kanhika.service.KanjiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/grade/{level}")
    public ResponseEntity<List<KanjiDTO>> getKanjisByGrade(@PathVariable int level) {
        return ResponseEntity.ok(kanjiService.getKanjisByGrade(level));
    }

    @GetMapping("/jlpt/{level}")
    public ResponseEntity<List<KanjiDTO>> getKanjisByJlpt(@PathVariable int level) {
        return ResponseEntity.ok(kanjiService.getKanjisByJlpt(level));
    }

    @GetMapping("/search/{input}")
    public ResponseEntity<List<KanjiDTO>> searchKanjis(@PathVariable String input) {
        return ResponseEntity.ok(kanjiService.searchKanjis(input));
    }
}
