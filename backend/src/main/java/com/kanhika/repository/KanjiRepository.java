package com.kanhika.repository;

import com.kanhika.model.Kanji;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface KanjiRepository extends JpaRepository<Kanji, Long> {
    // Return the infos about a kanji by String
    Optional<Kanji> findByKanji(String kanji);

    // Return the infos about a kanji by id
    Kanji findById(int id);

    // Return the id of all kanjis matching grade and jlpt
    @Query("""
        SELECT k.id
        FROM Kanji k
        WHERE (:grade IS NULL OR k.grade = :grade)
        AND (:jlpt IS NULL OR k.jlpt = :jlpt)
    """)
    List<Integer> findAllIdByGradeAndJlpt(Integer grade, Integer jlpt);
}
