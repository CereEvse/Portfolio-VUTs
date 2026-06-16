package ru.accouting.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import ru.accouting.student.model.AchievementType;

import java.time.LocalDate;

@Data
public class AchievementRequestDto {

    @NotNull(message = "Дата получения обязательна")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate achievementDate;

    @NotBlank(message = "Описание обязательно")
    private String description;

    @NotNull(message = "Тип достижения обязателен")
    private AchievementType type;

    // Файл может быть необязательным при создании (потом можно добавить)
    private MultipartFile file;
}
