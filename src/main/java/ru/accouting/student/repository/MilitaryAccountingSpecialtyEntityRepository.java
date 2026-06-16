package ru.accouting.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.accouting.student.model.MilitaryAccountingSpecialtyEntity;

public interface MilitaryAccountingSpecialtyEntityRepository
        extends JpaRepository<MilitaryAccountingSpecialtyEntity, Long> {

    boolean existsByCode(String code);
}
