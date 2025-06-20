package com.kanhika.repository;

import com.kanhika.model.Kanji;
import com.kanhika.model.Score;
import com.kanhika.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    // Get the score for a given user and kanji
    Optional<Score> findByUserAndKanji(User user, Kanji kanji);
}
