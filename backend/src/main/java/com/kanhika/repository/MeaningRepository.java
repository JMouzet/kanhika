package com.kanhika.repository;

import com.kanhika.model.Kanji;
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
        FROM KanjiMeaning m
        WHERE m.kanji.kanji = :kanji
        ORDER BY m.isDefault DESC
    """)
    List<Meaning> findAllByKanji(@Param("kanji") String kanji);

    // Return a list of kanji matching with its exact meaning
    // Default comes first
    @Query("""
        SELECT m.kanji
        FROM KanjiMeaning m
        WHERE m.meaning.meaning = :input
        ORDER BY m.isDefault DESC
    """)
    List<Kanji> findAllByMeaningExact(@Param("input") String input);

    // Return a list of kanji matching with its meaning starting with
    // Default comes first
    @Query("""
        SELECT m.kanji
        FROM KanjiMeaning m
        WHERE m.meaning.meaning LIKE :input%
        ORDER BY m.isDefault DESC
    """)
    List<Kanji> findAllByMeaningStarting(@Param("input") String input);

    // Return a list of kanji matching with its meaning containing
    // Default comes first
    @Query("""
        SELECT m.kanji
        FROM KanjiMeaning m
        WHERE m.meaning.meaning LIKE %:input%
        ORDER BY m.isDefault DESC
    """)
    List<Kanji> findAllByMeaningContains(@Param("input") String input);
}
