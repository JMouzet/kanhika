package com.kanhika.repository;

import com.kanhika.model.Question;
import com.kanhika.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    // Get last unanswered question
    Optional<Question> findByQuizAndGivenAnswerNull(Quiz quiz);

    // Get all already answered questions
    List<Question> findByQuizAndGivenAnswerNotNull(Quiz quiz);

    // Get already answered question by id
    Optional<Question> findByIdAndGivenAnswerNull(int questionId);

    // Test correct answer with the given answer
    @Query("""
        SELECT CASE WHEN q.givenAnswer = q.correctAnswer THEN true ELSE false END
        FROM Question q
        WHERE q = :question
    """)
    boolean isAnswerCorrect(Question question);

    // Count the number of completed question for a specific quiz
    int countByQuizAndGivenAnswerNotNull(Quiz quiz);

    // Count the number of right answer during the quiz
    @Query("""
        SELECT COUNT(q) FROM Question q
        WHERE q.quiz = :quiz AND q.givenAnswer = q.correctAnswer
    """)
    int countCorrectAnswer(@Param("quiz") Quiz quiz);
}
