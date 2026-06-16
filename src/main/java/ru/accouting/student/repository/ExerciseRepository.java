package ru.accouting.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.accouting.student.model.Exercise;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByCategory(Exercise.ExerciseCategory category);
    Optional<Exercise> findByNumber(Integer number);
    Optional<Exercise> findByName(String name);
}