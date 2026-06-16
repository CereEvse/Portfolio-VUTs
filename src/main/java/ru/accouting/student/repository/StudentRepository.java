package ru.accouting.student.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.accouting.student.model.*;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    // Стандартные CRUD-методы уже есть в JpaRepository

    List<Student> findAll();

    @Query("SELECT s FROM student s WHERE s.idUser = :idUser")
    Optional<Student> findByIdUser(@Param("idUser") Long idUser);

    List<Student> findByStatus(StudentStatus studentStatus);

    boolean existsByMilitaryAccountingSpecialty(MilitaryAccountingSpecialtyEntity vus);

    List<Student> findByMilitaryAccountingSpecialty(MilitaryAccountingSpecialtyEntity vus);

    // StudentRepository
    List<Student> findByGroupStudentIn(List<StudyGroup> groups);
    boolean existsByGroupStudentIn(List<StudyGroup> groups);
    boolean existsByGroupStudent_Specialty(SpecialtyCodeInstitute specialty);

    List<Student> findByGroupStudent_Specialty(SpecialtyCodeInstitute specialty);

    boolean existsByGroupStudent(StudyGroup group);

    List<Student> findByGroupStudent(StudyGroup group);

    boolean existsByMilitaryCommissariat(MilitaryCommissariatEntity mc);

    List<Student> findByMilitaryCommissariat(MilitaryCommissariatEntity mc);

    List<Student> findAllByOrderByGroupStudent_NameGroupAscLastNameAsc();

    List<Student> findByStatusAndApplicationYear(StudentStatus status, Integer applicationYear);

    List<Student> findAllByApplicationYear(int applicationYear);

    List<Student> findByCourseAndGroupStudent_Specialty_IdAndPlatoon_NamePlatoon(
            Integer course,
            Long specialtyId,
            String namePlatoon
    );

    boolean existsByPlatoon(Platoon platoon);

    List<Student> findByPlatoon(Platoon platoon);


    /**
     * Поиск с фильтрами по:
     *  - названию группы (contains, ignore case)
     *  - курсу (если передан > 0, иначе игнор)
     *  - названию специальности (contains, ignore case)
     *  - институту (contains, ignore case)
     *  - коду ВУС (contains, ignore case)
     *
     * Все фильтры опциональны: если параметр пустой / null, он не используется.
     */
    @Query("""
       SELECT s FROM student s
       LEFT JOIN s.groupStudent g
       LEFT JOIN g.specialty sp
       LEFT JOIN s.militaryAccountingSpecialty vus
       WHERE (:groupName IS NULL OR :groupName = '' OR LOWER(g.nameGroup) LIKE LOWER(CONCAT('%', :groupName, '%')))
         AND (:course IS NULL OR :course = 0 OR s.course = :course)
         AND (:specialtyTitle IS NULL OR :specialtyTitle = '' OR LOWER(sp.titleSpecialty) LIKE LOWER(CONCAT('%', :specialtyTitle, '%')))
         AND (:institute IS NULL OR :institute = '' OR LOWER(sp.institute) LIKE LOWER(CONCAT('%', :institute, '%')))
         AND (:vusCode IS NULL OR :vusCode = '' OR LOWER(vus.code) LIKE LOWER(CONCAT('%', :vusCode, '%')))
       """)
    List<Student> findByFilters(String groupName,
                                Integer course,
                                String specialtyTitle,
                                String institute,
                                String vusCode,
                                Sort sort);

    @Query("""
           select s
           from student s
           where
               (:specialtyCode is null or :specialtyCode = '' 
                   or s.groupStudent.specialty.codeSpecialty = :specialtyCode)
           and (:institute is null or :institute = '' 
                   or s.groupStudent.specialty.institute = :institute)
           and (:course is null or s.course = :course)
           and (:groupName is null or :groupName = '' 
                   or s.groupStudent.nameGroup = :groupName)
           """)
    List<Student> findByCompetitionFilters(
            @Param("specialtyCode") String specialtyCode,
            @Param("institute") String institute,
            @Param("course") Integer course,
            @Param("groupName") String groupName,
            Sort sort
    );

    @Query("""
       select s
       from student s
       where
           (:specialtyCode is null or :specialtyCode = '' 
               or s.groupStudent.specialty.codeSpecialty = :specialtyCode)
       and (:institute is null or :institute = '' 
               or s.groupStudent.specialty.institute = :institute)
       and (:course is null or s.course = :course)
       and (:groupName is null or :groupName = '' 
               or s.groupStudent.nameGroup = :groupName)
       and (
               :hasNote is null or :hasNote = ''
               or (:hasNote = 'with' 
                   and s.noteStudent is not null and trim(s.noteStudent) <> '')
               or (:hasNote = 'without' 
                   and (s.noteStudent is null or trim(s.noteStudent) = ''))
           )
       """)
    List<Student> findByCompetitionFilters(
            @Param("specialtyCode") String specialtyCode,
            @Param("institute") String institute,
            @Param("course") Integer course,
            @Param("groupName") String groupName,
            @Param("hasNote") String hasNote,
            Sort sort
    );

//    @Query("""
//    select s
//    from student s
//    join s.groupStudent gs
//    join s.platoon p
//    where (:specialtyId is null or gs.specialty.id = :specialtyId)
//      and (:platoonId is null or p.id = :platoonId)
//""")
//    List<Student> filterStudents(@Param("specialtyId") Long specialtyId,
//                                 @Param("platoonId") Long platoonId);

    @Query("""
    SELECT s FROM student s 
    WHERE (:specialtyId IS NULL OR 
           s.platoon.specialty.id = :specialtyId)
    AND (:platoonId IS NULL OR s.platoon.id = :platoonId)
    ORDER BY s.lastName, s.firstName
""")
    List<Student> filterStudents(@Param("specialtyId") Long specialtyId, @Param("platoonId") Long platoonId);

    @Query("SELECT DISTINCT s.applicationYear FROM student s WHERE s.applicationYear IS NOT NULL ORDER BY s.applicationYear DESC")
    List<Integer> findDistinctApplicationYears();

    @Query("SELECT DISTINCT s.status FROM student s WHERE s.status IS NOT NULL ORDER BY s.status")
    List<StudentStatus> findDistinctStatuses();

}
