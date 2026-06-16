// StudentTrainingCampPeriodRepository.java
package ru.accouting.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.accouting.student.model.StudentTrainingCampPeriod;
import java.util.Optional;

public interface StudentTrainingCampPeriodRepository extends JpaRepository<StudentTrainingCampPeriod, Long> {
    Optional<StudentTrainingCampPeriod> findByStudent_IdStudent(Long studentId);
}