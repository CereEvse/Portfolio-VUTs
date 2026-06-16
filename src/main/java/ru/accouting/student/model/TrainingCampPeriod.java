package ru.accouting.student.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "training_camp_period")
@Getter
@Setter
public class TrainingCampPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "training_camp_period_seq")
    @SequenceGenerator(name = "training_camp_period_seq", sequenceName = "training_camp_period_seq", allocationSize = 1)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
}