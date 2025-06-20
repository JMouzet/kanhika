package com.kanhika.service;

import com.kanhika.dto.quiz.QuizInDTO;
import com.kanhika.dto.quiz.QuizOutDTO;
import com.kanhika.exception.InvalidFormatException;
import com.kanhika.model.Quiz;
import com.kanhika.model.User;
import com.kanhika.repository.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final KanjiRepository kanjiRepository;
    private final MeaningRepository meaningRepository;
    private final ReadingRepository readingRepository;
    private final UserRepository userRepository;
    private final ScoreRepository scoreRepository;

    public QuizService(QuizRepository quizRepository,
                       QuestionRepository questionRepository,
                       KanjiRepository kanjiRepository,
                       MeaningRepository meaningRepository,
                       ReadingRepository readingRepository,
                       UserRepository userRepository,
                       ScoreRepository scoreRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.kanjiRepository = kanjiRepository;
        this.meaningRepository = meaningRepository;
        this.readingRepository = readingRepository;
        this.userRepository = userRepository;
        this.scoreRepository = scoreRepository;
    }

    public QuizOutDTO getQuiz(String username) {
        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));

        // Get all uncleared quizzes
        List<Quiz> quizzes = quizRepository.findAllQuizByUserAndDoneFalseOrderByCreatedAtDesc(user);
        // If no quizzes, return -1
        if (quizzes.isEmpty()) {
            return new QuizOutDTO(-1);
        }

        // Return the last quiz id
        return new QuizOutDTO(quizzes.getFirst().getId());
    }

    public QuizOutDTO makeQuiz(String username, QuizInDTO request) {
        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));

        // Check request validity
        String[] questionsType = {"meaning", "reading", "mix"};
        if (!Arrays.asList(questionsType).contains(request.questions_type())) {
            throw new InvalidFormatException("Questions type must be one of the following: " + Arrays.toString(questionsType));
        }

        String[] scope = {"grade", "jlpt", "all"};
        if (!Arrays.asList(scope).contains(request.difficulty_type())) {
            throw new InvalidFormatException("Difficulty type must be one of the following: " + Arrays.toString(scope));
        }
        switch (request.difficulty_type()) {
            case "grade":
                // Grade level must be between 1 and 6 or 8
                if (request.difficulty_number() < 1 || request.difficulty_number() > 8 || request.difficulty_number() == 7) {
                    throw new InvalidFormatException("Difficulty number for type grade must be between 1 and 6 or 8");
                }
                break;
            case "jlpt":
                // JLPT level must be between 5 and 1
                if (request.difficulty_number() < 1 || request.difficulty_number() > 5) {
                    throw new InvalidFormatException("Difficulty number for type jlpt must be between 5 and 1");
                }
        }

        // Close uncleared quizzes
        List<Quiz> uncleared = quizRepository.findAllQuizByUserAndDoneFalseOrderByCreatedAtDesc(user);
        uncleared.forEach(q -> {
            q.setDone(true);
            quizRepository.save(q);
        });

        // Create the quiz object
        Quiz quiz = new Quiz();

        // Set the quiz properties
        quiz.setUser(user);
        quiz.setQuestionsNumber(request.questions_number());
        quiz.setQuestionsType(request.questions_type());
        quiz.setQuiz_scope(request.difficulty_type());
        quiz.setQuiz_level(request.difficulty_number());

        // Save the quiz
        quizRepository.save(quiz);

        return new QuizOutDTO(quiz.getId());
    }
}
