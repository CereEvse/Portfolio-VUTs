package ru.accouting.student.dto;

import lombok.Data;

@Data
public class MilitaryDetailsDto {
    private OrderDto admissionOrder;
    private OrderDto rankAssignmentOrder;
    private OathDto oath;
    private TrainingCampPeriodDto trainingCampPeriod;
}
