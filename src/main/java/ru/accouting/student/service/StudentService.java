package ru.accouting.student.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.accouting.student.dto.StudentCredentialsRow;
import ru.accouting.student.model.Student;
import ru.accouting.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    CreatedStudentResponse addStudent(Student student);
    List<Student> getAllStudents();
    Optional<Student> getStudentById(Long id);
    Optional<Student> putStudentById(Long id, Student updateStudent);
    void deleteStudentById(Long id);
    List<StudentCredentialsRow> getCredentialsRowsAndDelete(List<Student> students);


}
