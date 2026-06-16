package ru.accouting.student.mapper;

import ru.accouting.student.dto.ContestProtocolRowDto;
import ru.accouting.student.model.PhysicalTraining;
import ru.accouting.student.model.Student;

import java.time.format.DateTimeFormatter;

public class ContestProtocolMapper {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static ContestProtocolRowDto toDto(Student student) {
        ContestProtocolRowDto dto = new ContestProtocolRowDto();

        dto.setStudentId(student.getIdStudent());

        String fullName = String.format("%s %s %s",
                student.getLastName(),
                student.getFirstName(),
                student.getPatronymic() != null ? student.getPatronymic() : ""
        ).trim();
        dto.setFullName(fullName);

        if (student.getBirthday() != null) {
            dto.setBirthDate(student.getBirthday().format(DATE_FORMAT));
        }

        if (student.getGroupStudent() != null
                && student.getGroupStudent().getSpecialty() != null) {
            dto.setSpecialtyCode(
                    student.getGroupStudent().getSpecialty().getCodeSpecialty()
            );
        }

        dto.setFitnessCategory(
                student.getFitnessCategory() != null ? student.getFitnessCategory().name() : null
        );
        dto.setPsychoCategory(
                student.getPsychoCategory() != null ? student.getPsychoCategory().name() : null
        );

        if (student.isOverOrEqual25()) {
            dto.setAgeGroup("Старше 25");
        } else if (student.isUnder25()) {
            dto.setAgeGroup("Младше 25");
        } else {
            dto.setAgeGroup("");
        }

        // --- ФИЗПОДГОТОВКА (БЕРЁМ ИЗ PHYSICAL_TRAINING) ---
        PhysicalTraining pt = student.getPhysicalTraining();
        if (pt != null) {
            dto.setStrengthExerciseNumber(pt.getStrengthExerciseNumber());
            dto.setStrengthResult(pt.getStrengthResult() != null ? pt.getStrengthResult().toString() : null);
            dto.setStrengthPoints(pt.getStrengthPoints());

            dto.setSpeedExerciseNumber(pt.getSpeedExerciseNumber());
            dto.setSpeedResult(pt.getSpeedResult() != null ? String.format("%.2f", pt.getSpeedResult()) : null);
            dto.setSpeedPoints(pt.getSpeedPoints());

            dto.setEnduranceExerciseNumber(pt.getEnduranceExerciseNumber());
            dto.setEnduranceResult(pt.getEnduranceResult() != null ? String.format("%.2f", pt.getEnduranceResult()) : null);
            dto.setEndurancePoints(pt.getEndurancePoints());

            dto.setTotalPoints(pt.getTotalPoints());
            dto.setPhysical100(pt.getFinalResult());

            dto.setPhysicalRequirementsMatch(
                    pt.getFinalResult() != null && pt.getFinalResult() >= 50 ? "Да" : "Нет"
            );
        } else {
            dto.setPhysicalRequirementsMatch("Нет");
        }

        dto.setHasQuotaRight(false);
        dto.setHasPriorityRight(false);

        if (student.getGrade100() != null) {
            dto.setAcademic100(student.getGrade100().intValue());
        }

        // Итоговый балл (физ + уч)
        int scorePhys = (pt != null && pt.getFinalResult() != null) ? pt.getFinalResult() : 0;
        int scoreAcad = (student.getGrade100() != null) ? student.getGrade100().intValue() : 0;
        dto.setFinalScore(scorePhys + scoreAcad);

        dto.setCommissionDecision("");

        return dto;
    }
}