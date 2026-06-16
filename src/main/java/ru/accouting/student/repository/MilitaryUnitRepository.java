// MilitaryUnitRepository.java
package ru.accouting.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.accouting.student.model.MilitaryUnit;
import java.util.Optional;

public interface MilitaryUnitRepository extends JpaRepository<MilitaryUnit, Long> {
    Optional<MilitaryUnit> findByName(String name);
}