package com.kanhika.controller;

import com.kanhika.dto.quiz.*;
import com.kanhika.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("")
    public ResponseEntity<QuizOutDTO> getQuiz(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(quizService.getQuiz(userDetails.getUsername()));
    }

    @PostMapping("")
    public ResponseEntity<QuizOutDTO> makeQuiz(@AuthenticationPrincipal UserDetails userDetails,
                                               @RequestBody @Valid QuizInDTO request) {
        return ResponseEntity.ok(quizService.makeQuiz(userDetails.getUsername(), request));
    }

    @GetMapping("/{quiz}/questions")
    public ResponseEntity<QuestionDTO> getQuestion(@AuthenticationPrincipal UserDetails userDetails,
                                                   @PathVariable int quiz) {
        return ResponseEntity.ok(quizService.getQuestion(userDetails.getUsername(), quiz));
    }

    @PostMapping("/{quiz}/questions/{question}")
    public ResponseEntity<ResultDTO> sendAnswer(@AuthenticationPrincipal UserDetails userDetails,
                                                @PathVariable int quiz,
                                                @PathVariable int question,
                                                @RequestBody @Valid AnswerDTO answer) {
        return ResponseEntity.ok(quizService.sendAnswer(userDetails.getUsername(), quiz, question, answer.answerNo()));
    }
}
