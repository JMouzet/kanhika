package com.kanhika.repository;

import com.kanhika.model.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReadingRepository extends JpaRepository<Reading, Long> {
    // Get a kanji kun readings
    @Query("""
        SELECT r.reading
        FROM KanjiReadings r
        WHERE r.kanji.kanji = :kanji
        AND r.isKun = true
    """)
    List<Reading> findAllKunByKanji(@Param("kanji") String kanji);

    // Get a kanji on readings
    @Query("""
        SELECT r.reading
        FROM KanjiReadings r
        WHERE r.kanji.kanji = :kanji
        AND r.isOn = true
    """)
    List<Reading> findAllOnByKanji(@Param("kanji") String kanji);
}
