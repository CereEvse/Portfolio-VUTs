package ru.accouting.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.accouting.student.model.Platoon;

import java.util.List;

public interface PlatoonRepository extends JpaRepository<Platoon, Long> {
    List<Platoon> findByNamePlatoon(String namePlatoon);
    List<Platoon> findByNamePlatoonContainingIgnoreCase(String namePlatoon);


}
