package ru.accouting.student.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.accouting.student.dto.MilitaryGradesDto;
import ru.accouting.student.model.Student;
import ru.accouting.student.model.StudentMilitaryGrades;
import ru.accouting.student.repository.StudentMilitaryGradesRepository;
import ru.accouting.student.repository.StudentRepository;

@Service
@RequiredArgsConstructor
public class MilitaryGradesService {

    private final StudentMilitaryGradesRepository gradesRepository;
    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public MilitaryGradesDto getGrades(Long studentId) {
        StudentMilitaryGrades grades = gradesRepository.findByStudent_IdStudent(studentId)
                .orElse(null);
        if (grades == null) {
            return new MilitaryGradesDto(); // все поля null
        }
        MilitaryGradesDto dto = new MilitaryGradesDto();
        dto.setMilitaryTechnicalGrade(grades.getMilitaryTechnicalGrade());
        dto.setTacticalSpecialGrade(grades.getTacticalSpecialGrade());
        dto.setGeneralMilitaryGrade(grades.getGeneralMilitaryGrade());
        dto.setTrainingCampsGrade(grades.getTrainingCampsGrade());
        dto.setFinalCertificationGrade(grades.getFinalCertificationGrade());
        return dto;
    }

    @Transactional
    public void saveGrades(Long studentId, MilitaryGradesDto dto) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));

        StudentMilitaryGrades grades = gradesRepository.findByStudent_IdStudent(studentId)
                .orElse(new StudentMilitaryGrades());
        grades.setStudent(student);
        grades.setMilitaryTechnicalGrade(dto.getMilitaryTechnicalGrade());
        grades.setTacticalSpecialGrade(dto.getTacticalSpecialGrade());
        grades.setGeneralMilitaryGrade(dto.getGeneralMilitaryGrade());
        grades.setTrainingCampsGrade(dto.getTrainingCampsGrade());
        grades.setFinalCertificationGrade(dto.getFinalCertificationGrade());
        gradesRepository.save(grades);
    }
}