package com.kanhika.repository;

import com.kanhika.model.Kanji;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KanjiRepository extends JpaRepository<Kanji, Long> {
    // Return the infos about a kanji
    Optional<Kanji> findByKanji(String kanji);

    // Return a list of kanji based on grade level
    List<Kanji> findAllByGrade(int grade);

    // Return a list of kanji based on JLPT level
    List<Kanji> findAllByJlpt(int jlpt);
}
