//package ru.accouting.student.service;
//
//import jakarta.transaction.Transactional;
//import org.springframework.stereotype.Service;
//import ru.accouting.student.dto.StudentAssessmentDto;
//import ru.accouting.student.model.Student;
//import ru.accouting.student.repository.StudentRepository;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.List;
//
//@Service
//public class StudentAssessmentService {
//
//    private final StudentRepository studentRepository;
//
//    public StudentAssessmentService(StudentRepository studentRepository) {
//        this.studentRepository = studentRepository;
//    }
//
//    public List<StudentAssessmentDto> getAllForAssessment() {
//        return studentRepository.findAllByOrderByGroupStudent_NameGroupAscLastNameAsc()
//                .stream()
//                .map(this::toDto)
//                .toList();
//    }
//
//    @Transactional
//    public void saveAll(List<StudentAssessmentDto> dtos) {
//        for (StudentAssessmentDto dto : dtos) {
//            Student s = studentRepository.findById(dto.getId())  // dto.id — это idStudent
//                    .orElseThrow();
//
//            s.setMedicalResult(dto.getMedicalResult());
//            s.setPsychoCategory(dto.getPsychoCategory());
//            s.setGrade5(dto.getGrade5());
//
//            if (dto.getGrade5() != null) {
//                BigDecimal g5 = dto.getGrade5();
//                BigDecimal grade100 = g5.subtract(BigDecimal.valueOf(2))
//                        .divide(BigDecimal.valueOf(3), 4, RoundingMode.HALF_UP)
//                        .multiply(BigDecimal.valueOf(100))
//                        .setScale(2, RoundingMode.HALF_UP);
//                s.setGrade100(grade100);
//            } else {
//                s.setGrade100(null);
//            }
//        }
//    }
//
//    private StudentAssessmentDto toDto(Student s) {
//        StudentAssessmentDto dto = new StudentAssessmentDto();
//        dto.setId(s.getIdStudent()); // БЫЛО getId()
//
//        String fullName = String.format("%s %s %s",
//                nullToEmpty(s.getLastName()),
//                nullToEmpty(s.getFirstName()),
//                nullToEmpty(s.getPatronymic()));
//        dto.setFullName(fullName.trim());
//
//        // если у группы есть, например, поле name или code — используй его
//        dto.setGroupName(
//                s.getGroupStudent() != null ? s.getGroupStudent().getNameGroup() : ""
//        ); // БЫЛО getGroupName()
//
//        dto.setMedicalResult(s.getMedicalResult());
//        dto.setPsychoCategory(s.getPsychoCategory());
//        dto.setGrade5(s.getGrade5());
//        dto.setGrade100(s.getGrade100());
//        return dto;
//    }
//
//    private String nullToEmpty(String s) {
//        return s == null ? "" : s;
//    }
//}
package ru.accouting.student.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.accouting.student.dto.StudentAssessmentDto;
import ru.accouting.student.model.PhysicalTraining;
import ru.accouting.student.model.Student;
import ru.accouting.student.model.StudentStatus;
import ru.accouting.student.repository.StudentRepository;
import ru.accouting.student.repository.StudentSpecifications;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentAssessmentService {

    private final StudentRepository studentRepository;

//    public List<StudentAssessmentDto> getAllForAssessment() {
//        return studentRepository.findAllByOrderByGroupStudent_NameGroupAscLastNameAsc()
//                .stream()
//                .map(this::toDto)
//                .toList();
//    }
    public List<StudentAssessmentDto> getAllForAssessment() {
        return studentRepository.findAllByOrderByGroupStudent_NameGroupAscLastNameAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void saveAll(List<StudentAssessmentDto> dtos) {
        for (StudentAssessmentDto dto : dtos) {
            Student s = studentRepository.findById(dto.getId())
                    .orElseThrow();

            s.setFitnessCategory(dto.getFitnessCategory());
            s.setPsychoCategory(dto.getPsychoCategory());

            if (dto.getGrade5() != null) {
                s.setGrade5(dto.getGrade5());
                s.setGrade100(to100Scale(dto.getGrade5()));
            } else {
                s.setGrade5(null);
                s.setGrade100(null);
            }

            recalculateAssessmentAndStatus(s);
        }
    }

    private void recalculateAssessmentAndStatus(Student s) {
        boolean hasRequiredCategories =
                s.getFitnessCategory() != null &&
                        s.getPsychoCategory() != null;

        if (!hasRequiredCategories) {
            s.setStatus(StudentStatus.NOT_PASSED);
            return;
        }

        boolean fitnessOk = s.getFitnessCategory() == Student.FitnessCategory.A
                || s.getFitnessCategory() == Student.FitnessCategory.B;

        boolean psychoOk = s.getPsychoCategory() == Student.PsychoCategory.I
                || s.getPsychoCategory() == Student.PsychoCategory.II
                || s.getPsychoCategory() == Student.PsychoCategory.III;

        if (!(fitnessOk && psychoOk)) {
            s.setStatus(StudentStatus.NOT_PASSED);
            return;
        }

        boolean hasPhysicalData = s.getPhysicalTraining() != null
                && s.getPhysicalTraining().getStrengthExerciseNumber() != null
                && s.getPhysicalTraining().getSpeedExerciseNumber() != null
                && s.getPhysicalTraining().getEnduranceExerciseNumber() != null
                && s.getPhysicalTraining().getStrengthResult() != null
                && s.getPhysicalTraining().getSpeedResult() != null
                && s.getPhysicalTraining().getEnduranceResult() != null;

        if (!hasPhysicalData) {
            if (s.getGrade5() == null) {
                s.setStatus(StudentStatus.PRELIMINARY_PASSED);
            }
            return;
        }

        if (!isPhysicalPassed(s)) {
            s.setStatus(StudentStatus.NOT_PASSED);
            return;
        }

        if (s.getGrade5() == null) {
            s.setStatus(StudentStatus.PRELIMINARY_PASSED);
            return;
        }

        if (s.getGrade5().compareTo(new BigDecimal("3.00")) > 0) {
            s.setStatus(StudentStatus.CONTEST_PASSED);
        } else {
            s.setStatus(StudentStatus.NOT_PASSED);
        }
    }

    private boolean isPhysicalPassed(Student s) {
        PhysicalTraining pt = s.getPhysicalTraining();
        if (pt == null) return false;

        boolean allExercisesPassed =
                pt.getStrengthPoints() != null && pt.getStrengthPoints() > 0 &&
                        pt.getSpeedPoints() != null && pt.getSpeedPoints() > 0 &&
                        pt.getEndurancePoints() != null && pt.getEndurancePoints() > 0;

        boolean totalPassed = s.isUnder25()
                ? pt.getTotalPoints() != null && pt.getTotalPoints() >= 120
                : pt.getTotalPoints() != null && pt.getTotalPoints() >= 110;

        boolean finalResultOk = pt.getFinalResult() != null && pt.getFinalResult() > 0;

        return allExercisesPassed && totalPassed && finalResultOk;
    }

    private StudentAssessmentDto toDto(Student s) {
        StudentAssessmentDto dto = new StudentAssessmentDto();
        dto.setId(s.getIdStudent());

        String fullName = String.format("%s %s %s",
                nullToEmpty(s.getLastName()),
                nullToEmpty(s.getFirstName()),
                nullToEmpty(s.getPatronymic()));
        dto.setFullName(fullName.trim());

        String groupName = "";
        if (s.getGroupStudent() != null) {
            // подстрой под реальное имя поля в StudyGroup
            groupName = nullToEmpty(s.getGroupStudent().getNameGroup());
        }
        dto.setGroupName(groupName);

        dto.setFitnessCategory(s.getFitnessCategory());
        dto.setPsychoCategory(s.getPsychoCategory());
        dto.setGrade5(s.getGrade5());
        dto.setGrade100(s.getGrade100());

        return dto;
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    /**
     * Перевод из 5‑балльной в 100‑балльную шкалу:
     * 3.00 = 0; 3.02 = 1; ...; 4.98 = 99; 5.00 = 100.
     */
    private BigDecimal to100Scale(BigDecimal grade5) {
        if (grade5 == null) {
            return null;
        }

        BigDecimal three = BigDecimal.valueOf(3.0);
        BigDecimal five  = BigDecimal.valueOf(5.0);

        if (grade5.compareTo(three) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);
        }
        if (grade5.compareTo(five) >= 0) {
            return BigDecimal.valueOf(100).setScale(2, RoundingMode.UNNECESSARY);
        }

        BigDecimal step = BigDecimal.valueOf(0.02);

        // steps = round((grade5 - 3.00) / 0.02)
        BigDecimal steps = grade5.subtract(three)
                .divide(step, 0, RoundingMode.HALF_UP);

        return steps.setScale(2, RoundingMode.UNNECESSARY);
    }

    public List<StudentAssessmentDto> getFilteredForAssessment(Integer year, String group) {
        Specification<Student> spec = StudentSpecifications.withFilters(year, group, null, null, null, null);
        return studentRepository.findAll(spec)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<Integer> getApplicationYears() {
        return studentRepository.findAll().stream()
                .map(Student::getApplicationYear)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }
}