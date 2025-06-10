package com.kanhika.service;

import com.kanhika.dto.kanji.KanjiDTO;
import com.kanhika.exception.ResourceNotFoundException;
import com.kanhika.model.Kanji;
import com.kanhika.model.Meaning;
import com.kanhika.model.Reading;
import com.kanhika.repository.KanjiRepository;
import com.kanhika.repository.MeaningRepository;
import com.kanhika.repository.ReadingRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KanjiService {

    private final KanjiRepository kanjiRepository;
    private final MeaningRepository meaningRepository;
    private final ReadingRepository readingRepository;

    public KanjiService(KanjiRepository kanjiRepository,
                        MeaningRepository meaningRepository,
                        ReadingRepository readingRepository) {
        this.kanjiRepository = kanjiRepository;
        this.meaningRepository = meaningRepository;
        this.readingRepository = readingRepository;
    }

    public KanjiDTO getKanji(String kanji) {
        Kanji kanjiInfo = kanjiRepository.findByKanji(kanji)
                .orElseThrow(() -> new ResourceNotFoundException("Kanji not found."));

        List<String> meanings = meaningRepository.findAllByKanji(kanji)
                .stream()
                .map(Meaning::getMeaning)
                .toList();
        List<String> kunReadings = readingRepository.findAllKunByKanji(kanji)
                .stream()
                .map(Reading::getReading)
                .toList();
        List<String> onReadings = readingRepository.findAllOnByKanji(kanji)
                .stream()
                .map(Reading::getReading)
                .toList();

        return new KanjiDTO(
                kanjiInfo.getKanji(),
                kanjiInfo.getGrade(),
                kanjiInfo.getJlpt(),
                kanjiInfo.getStrokeCount(),
                meanings,
                kunReadings,
                onReadings
        );
    }
}
