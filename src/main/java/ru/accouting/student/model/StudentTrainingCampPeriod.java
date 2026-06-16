package ru.accouting.student.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "student_training_camp_period",
        uniqueConstraints = @UniqueConstraint(columnNames = "student_id"))
@Getter
@Setter
public class StudentTrainingCampPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stcp_seq")
    @SequenceGenerator(name = "stcp_seq", sequenceName = "stcp_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_camp_period_id", nullable = false)
    private TrainingCampPeriod trainingCampPeriod;
}