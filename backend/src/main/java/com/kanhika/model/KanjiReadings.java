package com.kanhika.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "kanjis_readings")
@Getter
@Setter
@NoArgsConstructor
public class KanjiReadings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "kanji")
    private Kanji kanji;

    @ManyToOne
    @JoinColumn(name = "reading")
    private Reading reading;

    private boolean isKun;
    private boolean isOn;
}
