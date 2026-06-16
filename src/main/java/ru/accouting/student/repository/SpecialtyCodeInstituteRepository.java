package ru.accouting.student.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.accouting.student.model.SpecialtyCodeInstitute;

import java.util.List;
import java.util.Optional;

public interface SpecialtyCodeInstituteRepository extends JpaRepository<SpecialtyCodeInstitute, Long> {
    boolean existsByCodeSpecialty(String codeSpeciality);
    // сортировка
    List<SpecialtyCodeInstitute> findAll(Sort sort);

    // фильтр по институту + сортировка
    List<SpecialtyCodeInstitute> findByInstitute(String institute, Sort sort);

    @Query("select distinct s.institute from SpecialtyCodeInstitute s order by s.institute")
    List<String> findDistinctInstitute();

    Optional<SpecialtyCodeInstitute> findByCodeSpecialty(String codeSpecialty);

    @Query("select distinct s.codeSpecialty from SpecialtyCodeInstitute s order by s.codeSpecialty")
    List<String> findDistinctCodeSpecialty();


}
