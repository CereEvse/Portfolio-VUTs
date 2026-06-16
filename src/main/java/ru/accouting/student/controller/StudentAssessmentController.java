package ru.accouting.student.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.accouting.student.dto.StudentsForm;
import ru.accouting.student.model.Student;
import ru.accouting.student.dto.StudentAssessmentDto;
import ru.accouting.student.repository.StudyGroupRepository;
import ru.accouting.student.service.StudentAssessmentService;

import java.util.List;

@Controller
@RequestMapping("/students/assessment")
@RequiredArgsConstructor
public class StudentAssessmentController {

    private final StudentAssessmentService assessmentService;
    private final StudyGroupRepository studyGroupRepository;

//    @GetMapping
//    public String showAssessmentPage(
//            @RequestParam(required = false) Integer year,
//            @RequestParam(required = false) String group,
//            Model model) {
//
//        List<Integer> applicationYears = assessmentService.getApplicationYears();
//        // 1. Используем переданные параметры или значения по умолчанию
//        int targetYear = (year != null)
//                ? year
//                : (applicationYears.isEmpty() ? java.time.LocalDate.now().getYear() : applicationYears.get(0));
//
//        // 2. Получаем отфильтрованный список через сервис (нужно обновить метод в сервисе)
//        List<StudentAssessmentDto> students = assessmentService.getFilteredForAssessment(targetYear, group);
//
//        model.addAttribute("studentsForm", new StudentsForm(students));
//
//        Integer maxYear = applicationYears.stream()
//                .mapToInt(Integer::intValue)
//                .max()
//                .orElse(0);
//        model.addAttribute("maxYear", maxYear);
//
//        // 3. Параметры для фильтров в HTML
//        model.addAttribute("selectedYear", targetYear);
//        model.addAttribute("selectedGroup", group);
//
//        // 4. Справочники для селектов
//        model.addAttribute("applicationYears", applicationYears);
//        model.addAttribute("groups", studyGroupRepository.findAll(Sort.by("nameGroup")));
////        model.addAttribute("medicalValues", Student.MedicalResult.values());
//        model.addAttribute("fitnessCategory", Student.FitnessCategory.values());
//        model.addAttribute("psychoValues", Student.PsychoCategory.values());
//
//        return "students/assessment";
//    }

    @GetMapping
    public String showAssessmentPage(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String group,
            Model model) {

        List<Integer> applicationYears = assessmentService.getApplicationYears();

        // 1. Логика выбора года по умолчанию: максимальный год из списка
        Integer targetYear;
        if (year != null) {
            // Используем переданный параметр
            targetYear = year;
        } else if (!applicationYears.isEmpty()) {
            // По умолчанию — МАКСИМАЛЬНЫЙ год из списка
            targetYear = applicationYears.stream()
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(java.time.LocalDate.now().getYear());
        } else {
            // Fallback на текущий год
            targetYear = java.time.LocalDate.now().getYear();
        }

        // 2. Получаем отфильтрованный список
        List<StudentAssessmentDto> students = assessmentService.getFilteredForAssessment(targetYear, group);

        model.addAttribute("studentsForm", new StudentsForm(students));

        // 3. Максимальный год для селекта
        Integer maxYear = applicationYears.stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
        model.addAttribute("maxYear", maxYear);

        // 4. Параметры для фильтров в HTML
        model.addAttribute("selectedYear", targetYear);
        model.addAttribute("selectedGroup", group);
        model.addAttribute("applicationYears", applicationYears);
        model.addAttribute("groups", studyGroupRepository.findAll(Sort.by("nameGroup")));

        // 5. Справочники для селектов (исправлено)
        model.addAttribute("fitnessCategories", Student.FitnessCategory.values());
        model.addAttribute("psychoCategories", Student.PsychoCategory.values());

        return "students/assessment";
    }

    @PostMapping
    public String saveAssessments(@ModelAttribute("studentsForm") StudentsForm form) {
        assessmentService.saveAll(form.getStudents());
        return "redirect:/students/assessment";
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        // Увеличь лимит до значения, которое заведомо больше количества твоих студентов
        binder.setAutoGrowCollectionLimit(2000);
    }

}