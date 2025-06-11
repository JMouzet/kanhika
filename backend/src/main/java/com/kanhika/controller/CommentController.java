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
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CommentDTO> editComment(@AuthenticationPrincipal UserDetails userDetails,
                                                  @PathVariable int id,
                                                  @RequestBody @Valid CommentPostDTO request) {
        return ResponseEntity.ok(commentService.editComment(userDetails.getUsername(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable int id) {
        commentService.deleteComment(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/vote/up")
    public ResponseEntity<Void> upvoteComment(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable int id) {
        commentService.voteComment(userDetails.getUsername(), id, true);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/vote/down")
    public ResponseEntity<Void> downvoteComment(@AuthenticationPrincipal UserDetails userDetails,
                                                @PathVariable int id) {
        commentService.voteComment(userDetails.getUsername(), id, false);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/vote")
    public ResponseEntity<Void> removeVoteComment(@AuthenticationPrincipal UserDetails userDetails,
                                                @PathVariable int id) {
        commentService.removeVoteComment(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
