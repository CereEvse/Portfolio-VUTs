package ru.accouting.student.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class OathDto {
    private Long id;
    private LocalDate oathDate;
    private String militaryUnitName; // название воинской части для отображения
    private Long militaryUnitId;     // ID части для внутреннего использования
}
