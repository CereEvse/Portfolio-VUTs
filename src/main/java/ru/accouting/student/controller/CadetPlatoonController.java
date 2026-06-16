package ru.accouting.student.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.accouting.student.model.Platoon;
import ru.accouting.student.model.Student;
import ru.accouting.student.service.CadetPlatoonService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cadets")
public class CadetPlatoonController {

    private final CadetPlatoonService cadetPlatoonService;

    @GetMapping
    public String cadetsPage(Model model) {
        List<Student> cadets = cadetPlatoonService.getCadets();
        List<Platoon> platoons = cadetPlatoonService.getPlatoons();

        model.addAttribute("cadets", cadets);
        model.addAttribute("platoons", platoons);
        model.addAttribute("selectedStudentId", null);
        model.addAttribute("selectedPlatoonId", null);

        return "cadets";
    }

    @PostMapping("/assign")
    public String assignPlatoon(@RequestParam Long studentId,
                                @RequestParam Long platoonId) {
        cadetPlatoonService.assignPlatoon(studentId, platoonId);
        return "redirect:/cadets";
    }
}