package ru.accouting.student.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "study_group")
@Getter
@Setter
public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "study_group_seq")
    @SequenceGenerator(name = "study_group_seq", sequenceName = "study_group_seq",
            initialValue = 1, allocationSize = 1)
    @Column(name = "id_group")
    private Long id;

    @Column(name = "name_group", nullable = false, unique = true)
    private String nameGroup; // "ЦИС-11", "ЦПИ-21" и т.д.
    @Override
    public String toString() {
        return nameGroup;
    }

    @Column(name = "course", nullable = false)
    private Integer course;   // 1 или 2

    @ManyToOne
    @JoinColumn(name = "specialty_id", nullable = false)
    private SpecialtyCodeInstitute specialty;
}
