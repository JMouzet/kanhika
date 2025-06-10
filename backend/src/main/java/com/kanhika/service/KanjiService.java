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

        return new KanjiDTO(
                kanjiInfo.getKanji(),
                kanjiInfo.getGrade(),
                kanjiInfo.getJlpt(),
                kanjiInfo.getStrokeCount(),
                meaningRepository.findAllByKanji(kanjiInfo.getKanji())
                        .stream()
                        .map(Meaning::getMeaning)
                        .toList(),
                readingRepository.findAllOnByKanji(kanjiInfo.getKanji())
                        .stream()
                        .map(Reading::getReading)
                        .toList(),
                readingRepository.findAllOnByKanji(kanjiInfo.getKanji())
                        .stream()
                        .map(Reading::getReading)
                        .toList()
        );
    }

    public List<KanjiDTO> getKanjisByGrade(int level) {
        List<Kanji> kanjis = kanjiRepository.findAllByGrade(level);

        return makeKanjiListDTO(kanjis);
    }

    public List<KanjiDTO> getKanjisByJlpt(int level) {
        List<Kanji> kanjis = kanjiRepository.findAllByJlpt(level);

        return makeKanjiListDTO(kanjis);
    }


    private List<KanjiDTO> makeKanjiListDTO(List<Kanji> kanjis) {
        return kanjis.stream()
                .map(kanji -> new KanjiDTO(
                        kanji.getKanji(),
                        kanji.getGrade(),
                        kanji.getJlpt(),
                        kanji.getStrokeCount(),
                        meaningRepository.findAllByKanji(kanji.getKanji())
                                .stream()
                                .map(Meaning::getMeaning)
                                .toList(),
                        readingRepository.findAllOnByKanji(kanji.getKanji())
                                .stream()
                                .map(Reading::getReading)
                                .toList(),
                        readingRepository.findAllOnByKanji(kanji.getKanji())
                                .stream()
                                .map(Reading::getReading)
                                .toList()
                )).toList();
    }
}
