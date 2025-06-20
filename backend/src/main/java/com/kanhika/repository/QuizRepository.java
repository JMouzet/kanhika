package com.kanhika.repository;

import com.kanhika.model.Quiz;
import com.kanhika.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    // Get uncleared quiz
    public List<Quiz> findAllQuizByUserAndDoneFalseOrderByCreatedAtDesc(User user);
}
