package com.kanhika.service;

import com.kanhika.dto.kanji.KanjiDTO;
import com.kanhika.exception.ResourceNotFoundException;
import com.kanhika.model.Kanji;
import com.kanhika.model.Meaning;
import com.kanhika.model.Reading;
import com.kanhika.repository.KanjiRepository;
import com.kanhika.repository.MeaningRepository;
import com.kanhika.repository.ReadingRepository;
import com.moji4j.MojiConverter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        return makeKanjiDTO(kanjiInfo);
    }

    public List<KanjiDTO> getKanjisByGrade(int level) {
        List<Kanji> kanjis = kanjiRepository.findAllByGrade(level);

        return makeListKanjiDTO(kanjis);
    }

    public List<KanjiDTO> getKanjisByJlpt(int level) {
        List<Kanji> kanjis = kanjiRepository.findAllByJlpt(level);

        return makeListKanjiDTO(kanjis);
    }

    public List<KanjiDTO> searchKanjis(String input) {
        MojiConverter converter = new MojiConverter();

        // Search by kanji
        List<KanjiDTO> kanjis = new ArrayList<>();
        try {
            kanjis = Collections.singletonList(getKanji(input));
        } catch (ResourceNotFoundException ignored) {}

        // Search by meaning exact
        List<KanjiDTO> meaningsExact = makeListKanjiDTO(meaningRepository.findAllByMeaningExact(input));

        // Search by reading exact
        List<KanjiDTO> readingsExact = makeListKanjiDTO(readingRepository.findAllByReadingExact(
                converter.convertRomajiToKatakana(
                        converter.convertKanaToRomaji(input))));

        // Search by meaning starting with
        List<KanjiDTO> meaningsStarting = makeListKanjiDTO(meaningRepository.findAllByMeaningStarting(input));

        // Search by reading starting with
        List<KanjiDTO> readingsStarting = makeListKanjiDTO(readingRepository.findAllByReadingStarting(
                converter.convertRomajiToKatakana(
                        converter.convertKanaToRomaji(input))));

        // Search by meaning contains
        List<KanjiDTO> meaningsContains = makeListKanjiDTO(meaningRepository.findAllByMeaningContains(input));

        // Search by reading contains
        List<KanjiDTO> readingsContains = makeListKanjiDTO(readingRepository.findAllByReadingContains(
                converter.convertRomajiToKatakana(
                        converter.convertKanaToRomaji(input))));

        return new ArrayList<>(
                Stream.of(
                        kanjis,
                        meaningsExact,
                        readingsExact,
                        meaningsStarting,
                        readingsStarting,
                        meaningsContains,
                        readingsContains
                )
                .flatMap(List::stream)
                .collect(Collectors.toMap(
                        KanjiDTO::kanji,
                        d -> d,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ))
                .values()
        );
    }


    private List<KanjiDTO> makeListKanjiDTO(List<Kanji> kanjis) {
        return kanjis.stream()
                .map(this::makeKanjiDTO)
                .toList();
    }

    private KanjiDTO makeKanjiDTO(Kanji kanji) {
        MojiConverter converter = new MojiConverter();

        // Get readings in kana form
        List<String> kunReadingsKana = readingRepository.findAllOnByKanji(kanji.getKanji())
                .stream()
                .map(Reading::getReading)
                .toList();
        List<String> onReadingsKana = readingRepository.findAllOnByKanji(kanji.getKanji())
                .stream()
                .map(Reading::getReading)
                .toList();
        // Get converted reading in Romaji form
        List<String> kunReadingRoma = kunReadingsKana.stream()
                .map(converter::convertKanaToRomaji)
                .toList();
        List<String> onReadingRoma = onReadingsKana.stream()
                .map(converter::convertKanaToRomaji)
                .toList();

        return new KanjiDTO(
                kanji.getKanji(),
                kanji.getGrade(),
                kanji.getJlpt(),
                kanji.getStrokeCount(),
                meaningRepository.findAllByKanji(kanji.getKanji())
                        .stream()
                        .map(Meaning::getMeaning)
                        .toList(),
                kunReadingsKana,
                kunReadingRoma,
                onReadingsKana,
                onReadingRoma
        );
    }
}
