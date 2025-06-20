package com.kanhika.repository;

import com.kanhika.model.Quiz;
import com.kanhika.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    // Get uncleared quiz
    List<Quiz> findAllByUserAndDoneFalseOrderByCreatedAtDesc(User user);

    // Get specified quiz
    Optional<Quiz> findByUserAndIdAndDoneFalse(User user, int quiz_id);
}
