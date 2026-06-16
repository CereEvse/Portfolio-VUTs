package ru.accouting.student.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.accouting.student.model.Platoon;
import ru.accouting.student.model.Student;
import ru.accouting.student.model.StudentStatus;
import ru.accouting.student.repository.PlatoonRepository;
import ru.accouting.student.repository.StudentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CadetPlatoonService {

    private final StudentRepository studentRepository;
    private final PlatoonRepository platoonRepository;

    public List<Student> getCadets() {
        return studentRepository.findByStatus(StudentStatus.CADET);
    }

    public List<Platoon> getPlatoons() {
        return platoonRepository.findAll();
    }

    @Transactional
    public void assignPlatoon(Long studentId, Long platoonId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Студент не найден: " + studentId));

        Platoon platoon = platoonRepository.findById(platoonId)
                .orElseThrow(() -> new IllegalArgumentException("Взвод не найден: " + platoonId));

        student.setPlatoon(platoon);
        studentRepository.save(student);
    }
}