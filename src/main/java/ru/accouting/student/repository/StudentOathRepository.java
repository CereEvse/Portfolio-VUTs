// StudentOathRepository.java
package ru.accouting.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.accouting.student.model.StudentOath;
import java.util.Optional;

public interface StudentOathRepository extends JpaRepository<StudentOath, Long> {
    Optional<StudentOath> findByStudent_IdStudent(Long studentId);
}