package ru.accouting.student.dto;

import lombok.Builder;
import lombok.Data;
import ru.accouting.student.model.AchievementType;

import java.time.LocalDate;

@Data
@Builder
public class AchievementResponseDto {

    private Long id;
    private Long studentId;
    private LocalDate achievementDate;
    private String description;
    private String filePath;
    private AchievementType type;
    private boolean hasFile;
}
