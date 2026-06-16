//package ru.accouting.student.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import ru.accouting.student.model.Platoon;
//import ru.accouting.student.model.Student;
//import ru.accouting.student.repository.MilitaryAccountingSpecialtyEntityRepository;
//import ru.accouting.student.repository.PlatoonRepository;
//import ru.accouting.student.repository.StudentRepository;
//
//import java.util.List;
//
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/platoons")
//public class PlatoonController {
//
//    private final StudentRepository studentRepository;
//    private final PlatoonRepository platoonRepository;
//    private final MilitaryAccountingSpecialtyEntityRepository specialtyRepository;
//
//    @GetMapping
//    public String listPlatoons(Model model) {
//        model.addAttribute("platoons", platoonRepository.findAll());
//        return "platoon-list";
//    }
//
//    @GetMapping("/create")
//    public String createForm(Model model) {
//        model.addAttribute("platoon", new Platoon());
//        model.addAttribute("specialties", specialtyRepository.findAll());
//        return "platoon-create";
//    }
//
//    @PostMapping("/create")
//    public String createPlatoon(@RequestParam String namePlatoon,
//                                @RequestParam Long specialtyId) {
//
//        Platoon platoon = new Platoon();
//        platoon.setNamePlatoon(namePlatoon);
//
//        platoon.setSpecialty(
//                specialtyRepository.findById(specialtyId)
//                        .orElseThrow(() -> new IllegalArgumentException("Программа подготовки не найдена: " + specialtyId))
//        );
//
//        platoonRepository.save(platoon);
//        return "redirect:/platoons";
//    }
//
//    @GetMapping("/delete/{id}")
//    public String deletePlatoon(@PathVariable Long id, RedirectAttributes redirectAttributes) {
//        Platoon platoon = platoonRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Взвод не найден: " + id));
//
//        List<Student> linkedStudents = studentRepository.findByPlatoon(platoon);
//
//        if (!linkedStudents.isEmpty()) {
//            redirectAttributes.addFlashAttribute("errorMessage",
//                    "Невозможно удалить взвод: с ним связаны студенты.");
//            redirectAttributes.addFlashAttribute("linkedStudents", linkedStudents);
//            return "redirect:/platoons";
//        }
//
//        platoonRepository.delete(platoon);
//        return "redirect:/platoons";
//    }
//
//    @GetMapping("/filter")
//    public String filter(
//            @RequestParam(required = false) Long specialtyId,
//            @RequestParam(required = false) Long platoonId,
//            Model model
//    ) {
//        List<Student> students = studentRepository.filterStudents(specialtyId, platoonId);
//
//        model.addAttribute("students", students);
//        model.addAttribute("specialties", specialtyRepository.findAll());
//        model.addAttribute("platoons", platoonRepository.findAll());
//
//        model.addAttribute("selectedSpecialtyId", specialtyId);
//        model.addAttribute("selectedPlatoonId", platoonId);
//
//        return "platoon-filter";
//    }
//}


package ru.accouting.student.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.accouting.student.model.Platoon;
import ru.accouting.student.model.Student;
import ru.accouting.student.repository.MilitaryAccountingSpecialtyEntityRepository;
import ru.accouting.student.repository.PlatoonRepository;
import ru.accouting.student.repository.StudentRepository;
import ru.accouting.student.service.PlatoonTransferService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/platoons")
public class PlatoonController {

    private final StudentRepository studentRepository;
    private final PlatoonRepository platoonRepository;
    private final MilitaryAccountingSpecialtyEntityRepository specialtyRepository;
    private final PlatoonTransferService platoonTransferService;

    @GetMapping
    public String listPlatoons(Model model) {
        model.addAttribute("platoons", platoonRepository.findAll());
        return "platoon-list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("platoon", new Platoon());
        model.addAttribute("specialties", specialtyRepository.findAll());
        return "platoon-create";
    }

    @PostMapping("/create")
    public String createPlatoon(@RequestParam String namePlatoon,
                                @RequestParam Long specialtyId) {

        Platoon platoon = new Platoon();
        platoon.setNamePlatoon(namePlatoon);

        platoon.setSpecialty(
                specialtyRepository.findById(specialtyId)
                        .orElseThrow(() -> new IllegalArgumentException("Программа подготовки не найдена: " + specialtyId))
        );

        platoonRepository.save(platoon);
        return "redirect:/platoons";
    }

    @PostMapping("/transfer-next-course")
    public String transferStudentsToNextCourse(RedirectAttributes redirectAttributes) {
        System.out.println("=== КНОПКА НАЖАТА ===");
        int updated = platoonTransferService.transferStudentsToNextCourse();
        redirectAttributes.addFlashAttribute("successMessage",
                "Обработано студентов: " + updated);
        return "redirect:/platoons/filter";
    }

    @GetMapping("/delete/{id}")
    public String deletePlatoon(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Platoon platoon = platoonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Взвод не найден: " + id));

        List<Student> linkedStudents = studentRepository.findByPlatoon(platoon);

        if (!linkedStudents.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Невозможно удалить взвод: с ним связаны студенты.");
            redirectAttributes.addFlashAttribute("linkedStudents", linkedStudents);
            return "redirect:/platoons";
        }

        platoonRepository.delete(platoon);
        return "redirect:/platoons";
    }

    @GetMapping("/filter")
    public String filter(
            @RequestParam(required = false) Long specialtyId,
            @RequestParam(required = false) Long platoonId,
            Model model
    ) {
        List<Student> students = studentRepository.filterStudents(specialtyId, platoonId);

        model.addAttribute("students", students);
        model.addAttribute("specialties", specialtyRepository.findAll());
        model.addAttribute("platoons", platoonRepository.findAll());

        model.addAttribute("selectedSpecialtyId", specialtyId);
        model.addAttribute("selectedPlatoonId", platoonId);

        return "platoon-filter";
    }
}