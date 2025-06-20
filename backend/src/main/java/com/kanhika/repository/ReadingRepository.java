package com.kanhika.repository;

import com.kanhika.model.Kanji;
import com.kanhika.model.Meaning;
import com.kanhika.model.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReadingRepository extends JpaRepository<Reading, Long> {
    // Get a kanji kun AND on readings
    @Query("""
        SELECT r.reading
        FROM KanjiReading r
        WHERE r.kanji.kanji = :kanji
    """)
    List<Reading> findAllByKanji(@Param("kanji") String kanji);

    // Get a kanji kun readings
    @Query("""
        SELECT r.reading
        FROM KanjiReading r
        WHERE r.kanji.kanji = :kanji
        AND r.isKun = true
    """)
    List<Reading> findAllKunByKanji(@Param("kanji") String kanji);

    // Get a kanji on readings
    @Query("""
        SELECT r.reading
        FROM KanjiReading r
        WHERE r.kanji.kanji = :kanji
        AND r.isOn = true
    """)
    List<Reading> findAllOnByKanji(@Param("kanji") String kanji);

    // Return a list of kanji matching with its exact reading
    // Default comes first
    @Query("""
        SELECT r.kanji
        FROM KanjiReading r
        WHERE r.reading.reading = :input
        AND (:grade = -1 OR :grade IS NULL OR r.kanji.grade = :grade)
        AND (:jlpt = -1 OR :jlpt IS NULL OR r.kanji.jlpt = :jlpt)
    """)
    List<Kanji> findAllByReadingExact(@Param("input") String input,
                                      @Param("grade") Integer grade,
                                      @Param("jlpt") Integer jlpt);

    // Return a list of kanji matching with its reading starting with
    // Default comes first
    @Query("""
        SELECT r.kanji
        FROM KanjiReading r
        WHERE r.reading.reading LIKE :input%
        AND (:grade IS NULL OR r.kanji.grade = :grade)
        AND (:jlpt IS NULL OR r.kanji.jlpt = :jlpt)
    """)
    List<Kanji> findAllByReadingStarting(@Param("input") String input,
                                         @Param("grade") Integer grade,
                                         @Param("jlpt") Integer jlpt);

    // Return a list of kanji matching with its reading containing
    // Default comes first
    @Query("""
        SELECT r.kanji
        FROM KanjiReading r
        WHERE r.reading.reading LIKE %:input%
        AND (:grade IS NULL OR r.kanji.grade = :grade)
        AND (:jlpt IS NULL OR r.kanji.jlpt = :jlpt)
    """)
    List<Kanji> findAllByReadingContains(@Param("input") String input,
                                         @Param("grade") Integer grade,
                                         @Param("jlpt") Integer jlpt);

    // Return the full list of readings based on grade or jlpt
    @Query("""
        SELECT r.reading
        FROM KanjiReading r
        WHERE (:grade IS NULL OR r.kanji.grade = :grade)
        AND (:jlpt IS NULL OR r.kanji.jlpt = :jlpt)
    """)
    List<Reading> findAllByGradeAndJlpt(@Param("grade") Integer grade,
                                        @Param("jlpt") Integer jlpt);

    // Return a list of kanji matching with its exact reading, grade and jlpt
    // No need to order
    @Query("""
        SELECT r.kanji
        FROM KanjiReading r
        WHERE r.reading.reading = :input
        AND (:grade IS NULL OR r.kanji.grade = :grade)
        AND (:jlpt IS NULL OR r.kanji.jlpt = :jlpt)
    """)
    List<Kanji> findAllByReadingAndGradeAndJlpt(@Param("input") String input,
                                                @Param("grade") Integer grade,
                                                @Param("jlpt") Integer jlpt);
}
