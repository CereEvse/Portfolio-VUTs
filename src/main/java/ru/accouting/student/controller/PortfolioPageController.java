package ru.accouting.student.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.accouting.student.config.SecurityUtils;
import ru.accouting.student.model.Exercise;
import ru.accouting.student.model.Student;
import ru.accouting.student.model.StudentStatus;
import ru.accouting.student.repository.ExerciseRepository;
import ru.accouting.student.repository.StudentRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class PortfolioPageController {

    private final StudentRepository studentRepository;
    private final SecurityUtils securityUtils;
    private final ExerciseRepository exerciseRepository;

    @GetMapping("/students/{studentId}/portfolio")
    public String viewStudentPortfolio(@PathVariable Long studentId, Model model) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));

        boolean isAdmin = securityUtils.isAdmin();
        model.addAttribute("isAdmin", isAdmin);

        // Проверка: портфолио доступно только для курсантов (статус CADET)
//        if (student.getStatus() != StudentStatus.CADET) {
//            model.addAttribute("student", student);
//            model.addAttribute("portfolioDisabled", true);
//            return "student-portfolio";
//        }
        model.addAttribute("portfolioDisabled", false);

        // Проверка прав доступа
        Long currentUserId = securityUtils.getCurrentUserId().orElse(null);
        if (!isAdmin) {
            if (currentUserId == null || !currentUserId.equals(student.getIdUser())) {
                throw new AccessDeniedException("Доступ запрещён");
            }
        }

        // Загружаем упражнения по категориям
        List<Exercise> strengthExercises = exerciseRepository.findByCategory(Exercise.ExerciseCategory.STRENGTH);
        List<Exercise> speedExercises = exerciseRepository.findByCategory(Exercise.ExerciseCategory.SPEED);
        List<Exercise> enduranceExercises = exerciseRepository.findByCategory(Exercise.ExerciseCategory.ENDURANCE);

        // Преобразуем списки в мапы для удобного поиска названия по номеру
        Map<Integer, String> strengthExerciseMap = strengthExercises.stream()
                .collect(Collectors.toMap(Exercise::getNumber, Exercise::getName));
        Map<Integer, String> speedExerciseMap = speedExercises.stream()
                .collect(Collectors.toMap(Exercise::getNumber, Exercise::getName));
        Map<Integer, String> enduranceExerciseMap = enduranceExercises.stream()
                .collect(Collectors.toMap(Exercise::getNumber, Exercise::getName));

        model.addAttribute("strengthExerciseMap", strengthExerciseMap);
        model.addAttribute("speedExerciseMap", speedExerciseMap);
        model.addAttribute("enduranceExerciseMap", enduranceExerciseMap);
        model.addAttribute("student", student);
        model.addAttribute("portfolioDisabled", false);
        model.addAttribute("currentUserId", currentUserId);
        return "student-portfolio";
    }
}
