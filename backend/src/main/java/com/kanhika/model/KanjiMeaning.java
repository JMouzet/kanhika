package com.kanhika.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "kanjis_meanings")
@Getter
@Setter
@NoArgsConstructor
public class KanjiMeaning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "kanji")
    private Kanji kanji;

    @ManyToOne
    @JoinColumn(name = "meaning")
    private Meaning meaning;

    private boolean isDefault;
}
