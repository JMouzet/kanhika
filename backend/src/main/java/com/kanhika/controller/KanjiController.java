package com.kanhika.controller;

import com.kanhika.dto.comment.CommentDTO;
import com.kanhika.dto.comment.CommentPostDTO;
import com.kanhika.dto.kanji.KanjiDTO;
import com.kanhika.dto.kanji.KanjiSearchDTO;
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

    @GetMapping("/search/{input}")
    public ResponseEntity<KanjiSearchDTO> searchKanjis(@PathVariable String input,
                                                       @RequestParam(required = false) Integer grade,
                                                       @RequestParam(required = false) Integer jlpt,
                                                       @RequestParam(name = "page", defaultValue = "1") Integer page) {
        return ResponseEntity.ok(kanjiService.searchKanjis(input, grade, jlpt, page));
    }

    @GetMapping("/search")
    public ResponseEntity<KanjiSearchDTO> searchKanjis(@RequestParam(required = false) Integer grade,
                                                       @RequestParam(required = false) Integer jlpt,
                                                       @RequestParam(name = "page", defaultValue = "1") Integer page) {
        return ResponseEntity.ok(kanjiService.searchKanjis("", grade, jlpt, page));
    }

    @GetMapping("/{kanji}/comments")
    public ResponseEntity<List<CommentDTO>> getComments(@AuthenticationPrincipal UserDetails userDetails,
                                                        @PathVariable String kanji) {
        return ResponseEntity.ok(commentService.getComments(userDetails.getUsername(), kanji));
    }

    @PostMapping("/{kanji}/comments")
    public ResponseEntity<CommentDTO> sendComment(@AuthenticationPrincipal UserDetails userDetails,
                                                  @PathVariable String kanji,
                                                  @RequestBody @Valid CommentPostDTO request) {
        return ResponseEntity.ok(commentService.sendComment(userDetails.getUsername(), kanji, request));
    }
}
