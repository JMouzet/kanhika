package com.kanhika.service;

import com.kanhika.dto.kanji.KanjiDTO;
import com.kanhika.dto.kanji.KanjiSearchDTO;
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

    public KanjiSearchDTO searchKanjis(String input, Integer grade, Integer jlpt, Integer page) {
        MojiConverter converter = new MojiConverter();

        // Search by kanji
        try {
            List<KanjiDTO> singleKanji = Collections.singletonList(getKanji(input));
            return new KanjiSearchDTO(
                    1,
                    1,
                    1,
                    1,
                    singleKanji
            );
        } catch (ResourceNotFoundException ignored) {}

        // Search by meaning exact
        List<Kanji> meaningsExact = meaningRepository.findAllByMeaningExact(input, grade, jlpt);

        // Search by reading exact
        List<Kanji> readingsExact = readingRepository.findAllByReadingExact(
                converter.convertRomajiToKatakana(
                        converter.convertKanaToRomaji(input)),
                grade, jlpt);

        // Search by meaning starting with
        List<Kanji> meaningsStarting = meaningRepository.findAllByMeaningStarting(input, grade, jlpt);

        // Search by reading starting with
        List<Kanji> readingsStarting = readingRepository.findAllByReadingStarting(
                converter.convertRomajiToKatakana(
                        converter.convertKanaToRomaji(input)),
                grade, jlpt);

        // Search by meaning contains
        List<Kanji> meaningsContains = meaningRepository.findAllByMeaningContains(input, grade, jlpt);

        // Search by reading contains
        List<Kanji> readingsContains = readingRepository.findAllByReadingContains(
                converter.convertRomajiToKatakana(
                        converter.convertKanaToRomaji(input)),
                grade, jlpt);

        List<Kanji> resultAll = new ArrayList<>(
                Stream.of(
                        meaningsExact,
                        readingsExact,
                        meaningsStarting,
                        readingsStarting,
                        meaningsContains,
                        readingsContains
                )
                .flatMap(List::stream)
                .collect(Collectors.toMap(
                        Kanji::getKanji,
                        d -> d,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ))
                .values()
        );

        int maxPages = (int) Math.ceil((double) resultAll.size() / 20);
        if (maxPages < 1) maxPages = 1;

        // Adapt page if below 0 or above maximum found
        if (page < 1) page = 1;
        if (page > maxPages) page = maxPages;
        int fromIndex = (page - 1) * 20;
        int toIndex = Math.min(fromIndex + 20, resultAll.size());

        if (fromIndex >= resultAll.size()) return new KanjiSearchDTO(
                0,
                page,
                resultAll.size(),
                maxPages,
                List.of()
        );

        List<Kanji> resultPaged = resultAll.subList(fromIndex, toIndex);

        return new KanjiSearchDTO(
                resultPaged.size(),
                page,
                resultAll.size(),
                maxPages,
                makeListKanjiDTO(resultPaged)
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
        List<String> kunReadingsKana = readingRepository.findAllKunByKanji(kanji.getKanji())
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
