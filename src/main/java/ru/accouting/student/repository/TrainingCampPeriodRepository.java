// TrainingCampPeriodRepository.java
package ru.accouting.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.accouting.student.model.TrainingCampPeriod;
import java.time.LocalDate;
import java.util.Optional;

public interface TrainingCampPeriodRepository extends JpaRepository<TrainingCampPeriod, Long> {
    Optional<TrainingCampPeriod> findByStartDateAndEndDate(LocalDate startDate, LocalDate endDate);
}