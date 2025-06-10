package com.kanhika.repository;

import com.kanhika.model.Kanji;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KanjiRepository extends JpaRepository<Kanji, Long> {
    // Return the infos about a kanji
    Optional<Kanji> findByKanji(String kanji);
}
