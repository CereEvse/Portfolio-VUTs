package ru.accouting.student.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.accouting.student.model.SpecialtyCodeInstitute;
import ru.accouting.student.model.StudyGroup;
import ru.accouting.student.repository.SpecialtyCodeInstituteRepository;
import ru.accouting.student.repository.StudentRepository;
import ru.accouting.student.repository.StudyGroupRepository;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/groups")
public class StudyGroupController {

    private final StudyGroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final SpecialtyCodeInstituteRepository specialtyRepository;

    // СПИСОК + сортировка + фильтр по институту + сообщение/связанные студенты
    @GetMapping
    public String list(@RequestParam(name = "sortField", required = false, defaultValue = "id") String sortField,
                       @RequestParam(name = "sortDir", required = false, defaultValue = "asc") String sortDir,
                       @RequestParam(name = "institute", required = false) String institute,
                       @RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "groupId", required = false) Long groupId,
                       Model model) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();

        List<StudyGroup> groups;
        if (institute != null && !institute.isBlank()) {
            // метод в репозитории: List<StudyGroup> findBySpecialty_Institute(String institute, Sort sort);
            groups = groupRepository.findBySpecialty_Institute(institute, sort);
        } else {
            groups = groupRepository.findAll(sort);
        }

        // список институтов для фильтра (distinct по полю institute у специальностей)
        List<String> institutes = specialtyRepository.findDistinctInstitute();

        model.addAttribute("groups", groups);
        model.addAttribute("institutes", institutes);
        model.addAttribute("selectedInstitute", institute);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("errorMessage", error);

        if (groupId != null) {
            StudyGroup group = groupRepository.findById(groupId).orElse(null);
            if (group != null) {
                model.addAttribute("linkedStudents",
                        studentRepository.findByGroupStudent(group));
            }
        }

        return "studyGroup-list";
    }

    // Страница создания группы
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        StudyGroup group = new StudyGroup();
        model.addAttribute("group", group);
        model.addAttribute("formTitle", "Добавление учебной группы");

        // список специальностей для <select>
        List<SpecialtyCodeInstitute> specialties =
                specialtyRepository.findAll(Sort.by("codeSpecialty"));
        model.addAttribute("specialties", specialties);

        return "studyGroup-form";
    }

    // Сохранение новой группы
    @PostMapping
    public String createGroup(@ModelAttribute("group") StudyGroup group) {
        groupRepository.save(group);
        return "redirect:/groups";
    }

    // Страница редактирования группы
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        StudyGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Группа не найдена: " + id));

        model.addAttribute("group", group);
        model.addAttribute("formTitle", "Редактирование учебной группы");

        List<SpecialtyCodeInstitute> specialties =
                specialtyRepository.findAll(Sort.by("codeSpecialty"));
        model.addAttribute("specialties", specialties);

        return "studyGroup-form";
    }

    // Сохранение изменений группы
    @PostMapping("/{id}")
    public String updateGroup(@PathVariable Long id,
                              @ModelAttribute("group") StudyGroup formGroup) {
        StudyGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Группа не найдена: " + id));

        group.setNameGroup(formGroup.getNameGroup());
        group.setCourse(formGroup.getCourse());
        group.setSpecialty(formGroup.getSpecialty());

        groupRepository.save(group);
        return "redirect:/groups";
    }

    // Удаление с проверкой связей
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        StudyGroup group = groupRepository.findById(id).orElseThrow();

        if (studentRepository.existsByGroupStudent(group)) {
            redirectAttributes.addAttribute("error",
                    "Невозможно удалить учебную группу: с ней связаны студенты.");
            redirectAttributes.addAttribute("groupId", id);
            return "redirect:/groups";
        }

        groupRepository.delete(group);
        return "redirect:/groups";
    }
}
