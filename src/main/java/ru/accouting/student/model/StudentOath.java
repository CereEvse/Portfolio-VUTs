package ru.accouting.student.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "student_oath",
        uniqueConstraints = @UniqueConstraint(columnNames = "student_id"))
@Getter
@Setter
public class StudentOath {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_oath_seq")
    @SequenceGenerator(name = "student_oath_seq", sequenceName = "student_oath_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oath_id", nullable = false)
    private MilitaryOath oath;
}