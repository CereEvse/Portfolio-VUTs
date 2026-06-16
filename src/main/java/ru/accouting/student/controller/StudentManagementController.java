package ru.accouting.student.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.accouting.student.dto.ContestProtocolRowDto;
import ru.accouting.student.model.StudentStatus;
import ru.accouting.student.service.StudentManagementService;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/student-management")
@RequiredArgsConstructor
public class StudentManagementController {
    private final StudentManagementService service;

    @GetMapping
    public String showManagementPage(@RequestParam(required = false) Integer year,
                                     @RequestParam(required = false) Long specialtyId,
                                     @RequestParam(required = false) Integer maxPerPlatoon,
                                     Model model) {
        int targetYear = (year != null) ? year : LocalDate.now().getYear();

        List<ContestProtocolRowDto> candidates = service.getCandidatesDtoByYearAndSpecialty(targetYear, specialtyId);
        List<ContestProtocolRowDto> rejected = service.getRejectedDtoByYearAndSpecialty(targetYear, specialtyId);

        model.addAttribute("candidates", candidates);
        model.addAttribute("rejected", rejected);
        model.addAttribute("year", targetYear);
        model.addAttribute("specialtyId", specialtyId);
        model.addAttribute("maxPerPlatoon", maxPerPlatoon);
        model.addAttribute("statuses", StudentStatus.values());
        model.addAttribute("specialties", service.getAllSpecialties());
        model.addAttribute("platoons", service.getAllPlatoons());
        model.addAttribute("availableYears", service.getAvailableYears());

        return "contest/management";
    }

    @PostMapping("/update")
    public String updateStudent(@RequestParam Long id,
                                @RequestParam StudentStatus status,
                                @RequestParam(required = false) Long platoonId,
                                @RequestParam Integer year,
                                @RequestParam(required = false) Long specialtyId) {

        service.updateStudentDetails(id, status, platoonId);

        // Сохраняем фильтры при редиректе
        String redirect = "redirect:/student-management?year=" + year;
        if (specialtyId != null) {
            redirect += "&specialtyId=" + specialtyId;
        }
        return redirect;
    }


    @PostMapping("/auto-assign")
    public String autoAssign(@RequestParam Integer year,
                             @RequestParam(required = false) Long specialtyId,
                             @RequestParam Integer maxPerPlatoon,
                             RedirectAttributes redirectAttributes) {

        service.autoAssignPlatoonsBySpecialty(year, maxPerPlatoon);

        redirectAttributes.addFlashAttribute("message",
                "Автоматическое распределение кандидатов по взводам выполнено успешно!");

        String redirect = "redirect:/student-management?year=" + year + "&maxPerPlatoon=" + maxPerPlatoon;
        if (specialtyId != null) {
            redirect += "&specialtyId=" + specialtyId;
        }
        return redirect;
    }
}
