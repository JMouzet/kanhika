package com.kanhika.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "meanings")
@Getter
@Setter
@NoArgsConstructor
public class Meaning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String meaning;
}
