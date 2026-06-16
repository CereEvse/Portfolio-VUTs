package ru.accouting.student.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.accouting.student.model.Student;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class StudentSpecifications {

    public static Specification<Student> withFilters(Integer year, String group, Integer course, String specialty, String institute, String vus) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (year != null) {
                predicates.add(cb.equal(root.get("applicationYear"), year));
            }
            if (group != null && !group.isBlank()) {
                predicates.add(cb.equal(root.get("groupStudent").get("nameGroup"), group));
            }
            if (course != null) {
                predicates.add(cb.equal(root.get("course"), course));
            }
            if (specialty != null && !specialty.isBlank()) {
                predicates.add(cb.equal(root.get("groupStudent").get("specialty").get("titleSpecialty"), specialty));
            }
            if (institute != null && !institute.isBlank()) {
                predicates.add(cb.equal(root.get("groupStudent").get("specialty").get("institute"), institute));
            }
            if (vus != null && !vus.isBlank()) {
                predicates.add(cb.equal(root.get("militaryAccountingSpecialty").get("code"), vus));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}