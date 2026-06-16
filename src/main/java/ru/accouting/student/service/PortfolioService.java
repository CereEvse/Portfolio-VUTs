package ru.accouting.student.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.accouting.student.dto.AchievementRequestDto;
import ru.accouting.student.dto.AchievementResponseDto;
import ru.accouting.student.model.PortfolioAchievement;
import ru.accouting.student.model.Student;
import ru.accouting.student.repository.PortfolioAchievementRepository;
import ru.accouting.student.repository.StudentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {

    private final PortfolioAchievementRepository achievementRepository;
    private final StudentRepository studentRepository;

    @Value("${app.portfolio.upload-dir:uploads/portfolio}")
    private String uploadDir;

    @Transactional(readOnly = true)
    public List<AchievementResponseDto> getStudentAchievements(Long studentId) {
        return achievementRepository.findAllByStudentIdStudentOrderByAchievementDateDesc(studentId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PortfolioAchievement getAchievementById(Long id) {
        return achievementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Достижение с id " + id + " не найдено"));
    }

    @Transactional
    public AchievementResponseDto createAchievement(Long studentId, AchievementRequestDto request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Студент с id " + studentId + " не найден"));

        PortfolioAchievement achievement = new PortfolioAchievement();
        achievement.setStudent(student);
        achievement.setAchievementDate(request.getAchievementDate());
        achievement.setDescription(request.getDescription());
        achievement.setType(request.getType());

        // Сохраняем файл, если он приложен
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            String filePath = saveFile(request.getFile());
            achievement.setFilePath(filePath);
        }

        PortfolioAchievement saved = achievementRepository.save(achievement);
        return toDto(saved);
    }

    @Transactional
    public AchievementResponseDto updateAchievement(Long id, AchievementRequestDto request) {
        PortfolioAchievement achievement = getAchievementById(id);

        achievement.setAchievementDate(request.getAchievementDate());
        achievement.setDescription(request.getDescription());
        achievement.setType(request.getType());

        // Если передан новый файл, заменяем старый
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            // Удаляем старый файл, если был
            deleteFileIfExists(achievement.getFilePath());
            String newFilePath = saveFile(request.getFile());
            achievement.setFilePath(newFilePath);
        }

        PortfolioAchievement saved = achievementRepository.save(achievement);
        return toDto(saved);
    }

    @Transactional
    public void deleteAchievement(Long id) {
        PortfolioAchievement achievement = getAchievementById(id);
        deleteFileIfExists(achievement.getFilePath());
        achievementRepository.delete(achievement);
    }

    public Path getFilePath(String relativePath) {
        return Paths.get(uploadDir).resolve(relativePath).normalize();
    }

    // -------------------- Вспомогательные методы --------------------

    private String saveFile(MultipartFile file) {
        try {
            // Создаём директорию, если её нет
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Генерируем уникальное имя файла
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID() + fileExtension;

            Path targetPath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return newFilename;  // сохраняем только имя файла (относительный путь)
        } catch (IOException e) {
            log.error("Ошибка при сохранении файла", e);
            throw new RuntimeException("Не удалось сохранить файл", e);
        }
    }

    private void deleteFileIfExists(String filename) {
        if (filename != null && !filename.isBlank()) {
            try {
                Path filePath = Paths.get(uploadDir).resolve(filename);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.warn("Не удалось удалить файл {}: {}", filename, e.getMessage());
            }
        }
    }

    public AchievementResponseDto toDto(PortfolioAchievement achievement) {
        return AchievementResponseDto.builder()
                .id(achievement.getIdPortfolio())
                .studentId(achievement.getStudent().getIdStudent())
                .achievementDate(achievement.getAchievementDate())
                .description(achievement.getDescription())
                .filePath(achievement.getFilePath())
                .type(achievement.getType())
                .hasFile(achievement.getFilePath() != null && !achievement.getFilePath().isBlank())
                .build();
    }

}
