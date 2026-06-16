package ru.accouting.student.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class MilitaryGradesDto {

    @Min(value = 2, message = "Оценка должна быть от 2 до 5")
    @Max(value = 5, message = "Оценка должна быть от 2 до 5")
    private Integer militaryTechnicalGrade;

    @Min(2) @Max(5)
    private Integer tacticalSpecialGrade;

    @Min(2) @Max(5)
    private Integer generalMilitaryGrade;

    @Min(2) @Max(5)
    private Integer trainingCampsGrade;

    @Min(2) @Max(5)
    private Integer finalCertificationGrade;
}