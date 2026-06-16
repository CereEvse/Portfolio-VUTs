package ru.accouting.student.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import ru.accouting.student.config.SecurityUtils;
import ru.accouting.student.dto.AchievementRequestDto;
import ru.accouting.student.dto.AchievementResponseDto;
import ru.accouting.student.model.PortfolioAchievement;
import ru.accouting.student.model.Student;
import ru.accouting.student.model.StudentStatus;
import ru.accouting.student.repository.StudentRepository;
import ru.accouting.student.service.PortfolioService;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final SecurityUtils securityUtils;
    private final StudentRepository studentRepository;

    /**
     * Единая проверка доступа к портфолио студента.
     * Только студенты со статусом CADET, и только если текущий пользователь является
     * администратором или самим студентом (по userId).
     */
    private void checkAccess(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));

        //Портфолио только у курсантов
//        if (student.getStatus() != StudentStatus.CADET) {
//            throw new AccessDeniedException("Портфолио доступно только для курсантов");
//        }

        if (!securityUtils.isAdmin()) {
            Long currentUserId = securityUtils.getCurrentUserId()
                    .orElseThrow(() -> new AccessDeniedException("Пользователь не авторизован"));
            if (!currentUserId.equals(student.getIdUser())) {
                throw new AccessDeniedException("Доступ запрещён");
            }
        }
    }

    @GetMapping("/students/{studentId}/achievements")
    public List<AchievementResponseDto> getStudentAchievements(@PathVariable Long studentId) {
        checkAccess(studentId);
        return portfolioService.getStudentAchievements(studentId);
    }

    @GetMapping("/achievements/{id}")
    public AchievementResponseDto getAchievement(@PathVariable Long id) {
        PortfolioAchievement achievement = portfolioService.getAchievementById(id);
        checkAccess(achievement.getStudent().getIdStudent());
        return portfolioService.toDto(achievement);
    }

    @PostMapping(value = "/students/{studentId}/achievements", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AchievementResponseDto> createAchievement(
            @PathVariable Long studentId,
            @Valid @ModelAttribute AchievementRequestDto request) {
        checkAccess(studentId);
        AchievementResponseDto created = portfolioService.createAchievement(studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(value = "/achievements/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AchievementResponseDto updateAchievement(
            @PathVariable Long id,
            @Valid @ModelAttribute AchievementRequestDto request) {
        PortfolioAchievement achievement = portfolioService.getAchievementById(id);
        checkAccess(achievement.getStudent().getIdStudent());
        return portfolioService.updateAchievement(id, request);
    }

    @DeleteMapping("/achievements/{id}")
    public ResponseEntity<Void> deleteAchievement(@PathVariable Long id) {
        PortfolioAchievement achievement = portfolioService.getAchievementById(id);
        checkAccess(achievement.getStudent().getIdStudent());
        portfolioService.deleteAchievement(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/achievements/{id}/file")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        PortfolioAchievement achievement = portfolioService.getAchievementById(id);
        // Доступ к файлу проверяется косвенно через доступ к достижению (уже проверено выше при получении)
        checkAccess(achievement.getStudent().getIdStudent());

        String filePath = achievement.getFilePath();
        if (filePath == null || filePath.isBlank()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path path = portfolioService.getFilePath(filePath);
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = "application/octet-stream";
            try {
                contentType = java.nio.file.Files.probeContentType(path);
            } catch (Exception ignored) {
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}