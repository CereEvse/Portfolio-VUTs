package ru.accouting.student.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "military_unit")
@Getter
@Setter
public class MilitaryUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "military_unit_seq")
    @SequenceGenerator(name = "military_unit_seq", sequenceName = "military_unit_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
