package com.kanhika.controller;

import com.kanhika.dto.comment.CommentDTO;
import com.kanhika.dto.comment.CommentPostDTO;
import com.kanhika.dto.kanji.KanjiDTO;
import com.kanhika.service.CommentService;
import com.kanhika.service.KanjiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kanjis")
public class KanjiController {

    private final KanjiService kanjiService;
    private final CommentService commentService;

    public KanjiController(KanjiService kanjiService,
                           CommentService commentService) {
        this.kanjiService = kanjiService;
        this.commentService = commentService;
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

    @GetMapping("/{kanji}/comments")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable String kanji) {
        return ResponseEntity.ok(commentService.getComments(kanji));
    }

    @PostMapping("/{kanji}/comments")
    public ResponseEntity<CommentDTO> sendComment(@AuthenticationPrincipal UserDetails userDetails,
                                  @PathVariable String kanji,
                                  @RequestBody @Valid CommentPostDTO request) {
        return ResponseEntity.ok(commentService.sendComment(userDetails.getUsername(), kanji, request));
    }
}
