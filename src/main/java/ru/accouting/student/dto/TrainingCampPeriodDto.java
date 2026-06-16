package ru.accouting.student.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TrainingCampPeriodDto {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
}
