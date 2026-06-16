package ru.accouting.student.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "military_oath")
@Getter
@Setter
public class MilitaryOath {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "military_oath_seq")
    @SequenceGenerator(name = "military_oath_seq", sequenceName = "military_oath_seq", allocationSize = 1)
    private Long id;

    @Column(name = "oath_date", nullable = false)
    private LocalDate oathDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "military_unit_id", nullable = false)
    private MilitaryUnit militaryUnit;
}