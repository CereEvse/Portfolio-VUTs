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
import ru.accouting.student.model.MilitaryAccountingSpecialtyEntity;
import ru.accouting.student.service.ContestProtocolService;
import ru.accouting.student.service.MilitaryAccountingSpecialtyService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/contest-protocol")
@RequiredArgsConstructor
public class ContestProtocolController {

    private final ContestProtocolService contestProtocolService;
    private final MilitaryAccountingSpecialtyService specialtyService;

    @GetMapping
    public String showContestProtocol(
            @RequestParam(name = "limit", defaultValue = "144") int limit,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "specialtyCode", required = false) String specialtyCode,
            Model model
    ) {
        int targetYear = (year != null) ? year : LocalDate.now().getYear();

        // ✅ Список всех программ подготовки
        List<MilitaryAccountingSpecialtyEntity> specialties = specialtyService.findAll();
        model.addAttribute("specialties", specialties);

        // ✅ 1. ВСЕГДА считаем конкурс на ОБЩЕМ списке
        List<ContestProtocolRowDto> allStudents = contestProtocolService.getAllRowsByYear(targetYear);

        List<ContestProtocolRowDto> allEligible = allStudents.stream()
                .filter(contestProtocolService::isEligible)
                .sorted(Comparator
                        .comparingInt(contestProtocolService::getPriorityIndex)
                        .thenComparing(ContestProtocolRowDto::getFinalScore, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        int totalEligibleCount = allEligible.size();
        int effectiveLimit = Math.min(limit, totalEligibleCount);

        // ✅ Разделяем общий конкурс
        List<ContestProtocolRowDto> allPassedWithinLimit = allEligible.stream()
                .limit(effectiveLimit)
                .map(row -> { row.setCommissionDecision("Рекомендовать для допуска"); return row; })
                .toList();

        List<ContestProtocolRowDto> allPassedOverLimit = allEligible.stream()
                .skip(effectiveLimit)
                .map(row -> { row.setCommissionDecision("Отказать по конкурсу"); return row; })
                .toList();

        List<ContestProtocolRowDto> allFailedPrelim = allStudents.stream()
                .filter(row -> !contestProtocolService.isEligible(row))
                .map(row -> { row.setCommissionDecision(contestProtocolService.generateFailedReasons(row)); return row; })
                .toList();

        // ✅ 2. ФИЛЬТР ПО ПРОГРАММЕ + ЛОГИКА "ALL"
        List<ContestProtocolRowDto> passedWithinLimit, passedOverLimit, failedPrelim;

        if (specialtyCode != null && !specialtyCode.trim().isEmpty() && !"ALL".equalsIgnoreCase(specialtyCode)) {
            // Конкретная программа + студенты с "ALL"
            Set<String> allowedCodes = Set.of(specialtyCode, "ALL");

            passedWithinLimit = allPassedWithinLimit.stream()
                    .filter(row -> row.getSpecialtyCode() != null && allowedCodes.contains(row.getSpecialtyCode()))
                    .toList();

            passedOverLimit = allPassedOverLimit.stream()
                    .filter(row -> row.getSpecialtyCode() != null && allowedCodes.contains(row.getSpecialtyCode()))
                    .toList();

            failedPrelim = allFailedPrelim.stream()
                    .filter(row -> row.getSpecialtyCode() != null && allowedCodes.contains(row.getSpecialtyCode()))
                    .toList();
        } else {
            // Все программы (включая "ALL")
            passedWithinLimit = allPassedWithinLimit;
            passedOverLimit = allPassedOverLimit;
            failedPrelim = allFailedPrelim;
        }

        // ✅ Статистика
        int eligibleCount = passedWithinLimit.size();
        int notEligibleCount = passedOverLimit.size() + failedPrelim.size();

        int selectedYear = (year != null) ? year : LocalDate.now().getYear();

        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("availableYears", contestProtocolService.getAvailableYears());
        // Модель
        model.addAttribute("limit", limit);
        model.addAttribute("effectiveLimit", effectiveLimit);
        model.addAttribute("totalEligibleCount", totalEligibleCount);
        model.addAttribute("eligibleCount", eligibleCount);
        model.addAttribute("notEligibleCount", notEligibleCount);
        model.addAttribute("selectedYear", targetYear);
        model.addAttribute("selectedSpecialtyCode", specialtyCode);
        model.addAttribute("passedWithinLimit", passedWithinLimit);
        model.addAttribute("passedOverLimit", passedOverLimit);
        model.addAttribute("failedPrelim", failedPrelim);

        return "contest/protocol";
    }

    @PostMapping("/update-statuses")
    public String updateStatuses(
            @RequestParam(name = "limit", defaultValue = "144") int limit,
            @RequestParam(name = "year", required = false) Integer year,
            RedirectAttributes redirectAttributes
    ) {
        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        contestProtocolService.updateContestStatuses(targetYear, limit);

        redirectAttributes.addFlashAttribute("successMessage", "Статусы студентов обновлены");
        return "redirect:/contest-protocol?year=" + targetYear + "&limit=" + limit;
    }


}