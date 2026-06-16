package ru.accouting.student.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.accouting.student.model.SpecialtyCodeInstitute;
import ru.accouting.student.model.Student;
import ru.accouting.student.model.StudyGroup;

import java.util.List;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    boolean existsByNameGroup(String nameGroup);
    List<StudyGroup> findBySpecialty(SpecialtyCodeInstitute specialty);

    List<StudyGroup> findAll(Sort sort);
    List<StudyGroup> findBySpecialty(SpecialtyCodeInstitute specialty, Sort sort);

    List<StudyGroup> findBySpecialty_Institute(String institute, Sort sort);

    @Query("select distinct s.institute from SpecialtyCodeInstitute s order by s.institute")
    List<String> findDistinctInstitute();



    // фильтр по институту через specialty.institute
    @Query("""
           select g
           from StudyGroup g
           where g.specialty.institute = :institute
           """)
    List<StudyGroup> findByInstitute(String institute, Sort sort);

    @Query("select distinct g.specialty.institute from StudyGroup g order by g.specialty.institute")
    List<String> findDistinctInstitutes();

    List<StudyGroup> findBySpecialtyIdAndCourse(Long specialtyId, Integer course);

    @Query("""
    SELECT DISTINCT sp.codeSpecialty 
    FROM SpecialtyCodeInstitute sp 
    JOIN StudyGroup sg ON sg.specialty.id = sp.id 
    ORDER BY sp.codeSpecialty
""")
    List<String> findDistinctSpecialtyCodes();
}
