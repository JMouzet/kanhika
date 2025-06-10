package com.kanhika.repository;

import com.kanhika.model.Meaning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MeaningRepository extends JpaRepository<Meaning, Long> {
    // Get a kanji meanings
    // Put the default meaning first
    @Query("""
        SELECT m.meaning
        FROM KanjiMeanings m
        WHERE m.kanji.kanji = :kanji
        ORDER BY m.isDefault DESC
    """)
    List<Meaning> findAllByKanji(@Param("kanji") String kanji);
}
