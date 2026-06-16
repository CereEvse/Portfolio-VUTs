package ru.accouting.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.accouting.student.model.StudentMilitaryGrades;

import java.util.Optional;

@Repository
public interface StudentMilitaryGradesRepository extends JpaRepository<StudentMilitaryGrades, Long> {

    Optional<StudentMilitaryGrades> findByStudent_IdStudent(Long studentId);
}