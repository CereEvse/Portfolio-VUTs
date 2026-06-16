// MilitaryOathRepository.java
package ru.accouting.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.accouting.student.model.MilitaryOath;
import java.time.LocalDate;
import java.util.Optional;

public interface MilitaryOathRepository extends JpaRepository<MilitaryOath, Long> {
    Optional<MilitaryOath> findByOathDateAndMilitaryUnit_Id(LocalDate oathDate, Long militaryUnitId);
}