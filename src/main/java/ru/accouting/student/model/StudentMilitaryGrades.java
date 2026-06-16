package ru.accouting.student.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "student_military_grades")
@Getter
@Setter
@ToString(exclude = "student")
public class StudentMilitaryGrades {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_military_grades_seq")
    @SequenceGenerator(name = "student_military_grades_seq", sequenceName = "student_military_grades_seq", allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", unique = true, nullable = false)
    private Student student;

    @Column(name = "military_technical_grade")
    private Integer militaryTechnicalGrade;       // Модуль военно-технической подготовки

    @Column(name = "tactical_special_grade")
    private Integer tacticalSpecialGrade;         // Модуль тактической и тактико-специальной подготовки

    @Column(name = "general_military_grade")
    private Integer generalMilitaryGrade;          // Модуль общевоенной подготовки

    @Column(name = "training_camps_grade")
    private Integer trainingCampsGrade;            // Учебные сборы

    @Column(name = "final_certification_grade")
    private Integer finalCertificationGrade;       // Итоговая аттестация
}