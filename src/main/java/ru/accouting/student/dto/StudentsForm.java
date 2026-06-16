package ru.accouting.student.dto;

import java.util.List;

public class StudentsForm {

    private List<StudentAssessmentDto> students;

    public StudentsForm() {
    }

    public StudentsForm(List<StudentAssessmentDto> students) {
        this.students = students;
    }

    public List<StudentAssessmentDto> getStudents() {
        return students;
    }

    public void setStudents(List<StudentAssessmentDto> students) {
        this.students = students;
    }
}
