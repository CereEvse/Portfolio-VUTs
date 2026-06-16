package ru.accouting.student.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.accouting.student.dto.StudentCredentialsRow;
import ru.accouting.student.model.*;
import ru.accouting.student.repository.*;
import ru.accouting.student.repository.StudentSpecifications;
import org.springframework.data.jpa.domain.Specification;
import ru.accouting.student.service.CreatedStudentResponse;
import ru.accouting.student.service.StudentService;
import ru.accouting.student.service.StudentStatusService;
import ru.accouting.student.util.ExcelExportUtil;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class StudentController {

    private final StudentRepository studentRepository;
    private final PlatoonRepository platoonRepository;
    private final SpecialtyCodeInstituteRepository specialtyCodeInstituteRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final MilitaryCommissariatRepository militaryCommissariatRepository;
    private final MilitaryAccountingSpecialtyEntityRepository militaryAccountingSpecialtyEntityRepository;
    private final StudentStatusService studentStatusService;
    private final StudentService studentService;
    private final StudentCredentialsRepository studentCredentialsRepository;


    /* ===================== ПОДАЛИ ЗАЯВЛЕНИЕ ===================== */

    // Список студентов с сортировкой и фильтрами
    @GetMapping("/student-applied")
    public String studentAppliedList(
            @RequestParam(name = "sortField", required = false, defaultValue = "lastName") String sortField,
            @RequestParam(name = "sortDir", required = false, defaultValue = "asc") String sortDir,

            @RequestParam(name = "applicationYear", required = false) Integer applicationYear, // Добавлено
            @RequestParam(name = "group", required = false) String groupName,
            @RequestParam(name = "course", required = false) Integer course,
            @RequestParam(name = "specialty", required = false) String specialtyTitle,
            @RequestParam(name = "institute", required = false) String institute,
            @RequestParam(name = "vus", required = false) String vusCode,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();

        // Используем Specification вместо старого findByFilters
        Specification<Student> spec = StudentSpecifications.withFilters(
                applicationYear, groupName, course, specialtyTitle, institute, vusCode
        ).and((root, query, cb) -> cb.equal(root.get("status"), StudentStatus.APPLIED));

        List<Student> students = studentRepository.findAll(spec, sort);

        List<Integer> applicationYears = studentRepository.findAll().stream()
                .map(Student::getApplicationYear)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .sorted()
                .toList();

        model.addAttribute("applicationYears", applicationYears);

        model.addAttribute("students", students);

        // данные для фильтров (оставляем как было)
        model.addAttribute("groups", studyGroupRepository.findAll(Sort.by("nameGroup")));
        model.addAttribute("courses", List.of(1, 2));
        model.addAttribute("specialties", specialtyCodeInstituteRepository.findAll(Sort.by("codeSpecialty")));
        model.addAttribute("institutes", specialtyCodeInstituteRepository.findDistinctInstitute());
        model.addAttribute("vusList", militaryAccountingSpecialtyEntityRepository.findAll(Sort.by("code")));

        // Передаем выбранный год в модель, чтобы сохранить его в селекте
        model.addAttribute("selectedApplicationYear", applicationYear);
        model.addAttribute("selectedGroup", groupName);
        model.addAttribute("selectedCourse", course);
        model.addAttribute("selectedSpecialty", specialtyTitle);
        model.addAttribute("selectedInstitute", institute);
        model.addAttribute("selectedVus", vusCode);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "student-main";
    }

    // Форма добавления студента
    @GetMapping("/student-applied/add")
    public String studentAddForm(Model model) {
        Student student = new Student();
        student.setPassport(new Passport()); // важно для биндинга *{passport.*}
        student.setStatus(StudentStatus.APPLIED);

        model.addAttribute("student", student);

        model.addAttribute("groups",
                studyGroupRepository.findAll(Sort.by("nameGroup")));
        model.addAttribute("vusList",
                militaryAccountingSpecialtyEntityRepository.findAll(Sort.by("code")));
        model.addAttribute("commissariats",
                militaryCommissariatRepository.findAll(Sort.by("name")));

        return "student-add";
    }

    // Обработка формы добавления
//    @Transactional
//    @PostMapping("/student-applied/add")
//    public String addStudent(@ModelAttribute Student student, RedirectAttributes redirectAttributes) {
//        StudyGroup g = student.getGroupStudent();
//        if (g != null) {
//            student.setCourse(g.getCourse());
//        }
//        student.setApplicationYear(java.time.LocalDate.now().getYear());
//        student.setStatus(StudentStatus.APPLIED);
//
//        if (student.getPassport() != null) {
//            Passport p = student.getPassport();
//            boolean allEmpty =
//                    (p.getNumberPassport() == null || p.getNumberPassport().isBlank()) &&
//                            (p.getSeriesPassport() == null || p.getSeriesPassport().isBlank()) &&
//                            p.getDatePassport() == null &&
//                            (p.getPlacePassport() == null || p.getPlacePassport().isBlank());
//
//            if (!allEmpty) {
//                p.setStudent(student);
//            } else {
//                student.setPassport(null);
//            }
//        }
//        CreatedStudentResponse result = studentService.addStudent(student);
//        redirectAttributes.addFlashAttribute("createdLogin", result.login());
//        redirectAttributes.addFlashAttribute("createdPassword", result.rawPassword());
//
//        return "redirect:/student-applied";
//    }

    @Transactional
    @PostMapping("/student-applied/add")
    public String addStudent(@ModelAttribute Student student, RedirectAttributes redirectAttributes) {
        StudyGroup g = student.getGroupStudent();
        if (g != null) {
            student.setCourse(g.getCourse());
        }
        student.setApplicationYear(java.time.LocalDate.now().getYear());
        student.setStatus(StudentStatus.APPLIED);

        if (student.getPassport() != null) {
            Passport p = student.getPassport();
            boolean allEmpty =
                    (p.getNumberPassport() == null || p.getNumberPassport().isBlank()) &&
                            (p.getSeriesPassport() == null || p.getSeriesPassport().isBlank()) &&
                            p.getDatePassport() == null &&
                            (p.getPlacePassport() == null || p.getPlacePassport().isBlank());

            if (!allEmpty) {
                p.setStudent(student);
            } else {
                student.setPassport(null);
            }
        }

        CreatedStudentResponse result = studentService.addStudent(student);
        redirectAttributes.addFlashAttribute("createdLogin", result.login());
        redirectAttributes.addFlashAttribute("createdPassword", result.rawPassword());

        return "redirect:/student-applied";
    }

//    @Transactional
//    @PostMapping("/student-applied/add")
//    public void addStudent(@ModelAttribute Student student,
//                           HttpServletResponse response) throws IOException {
//
//        StudyGroup g = student.getGroupStudent();
//        if (g != null) {
//            student.setCourse(g.getCourse());
//        }
//        student.setApplicationYear(java.time.LocalDate.now().getYear());
//        student.setStatus(StudentStatus.APPLIED);
//
//        if (student.getPassport() != null) {
//            Passport p = student.getPassport();
//            boolean allEmpty =
//                    (p.getNumberPassport() == null || p.getNumberPassport().isBlank()) &&
//                            (p.getSeriesPassport() == null || p.getSeriesPassport().isBlank()) &&
//                            p.getDatePassport() == null &&
//                            (p.getPlacePassport() == null || p.getPlacePassport().isBlank());
//
//            if (!allEmpty) {
//                p.setStudent(student);
//            } else {
//                student.setPassport(null);
//            }
//        }
//
//        CreatedStudentResponse result = studentService.addStudent(student);
//
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setHeader("Content-Disposition", "attachment; filename=student-credentials.xlsx");
//
//        ExcelExportUtil.exportCredentials(
//                List.of(new StudentCredentialsRow(
//                        result.student().getLastName(),
//                        result.student().getFirstName(),
//                        result.student().getPatronymic(),
//                        result.login(),
//                        result.rawPassword()
//                )),
//                response.getOutputStream()
//        );
//    }

//    @GetMapping("/student-applied/export-credentials")
//    public void exportCredentials(
//            @RequestParam(name = "applicationYear", required = false) Integer applicationYear,
//            @RequestParam(name = "group", required = false) String groupName,
//            @RequestParam(name = "course", required = false) Integer course,
//            @RequestParam(name = "specialty", required = false) String specialtyTitle,
//            @RequestParam(name = "institute", required = false) String institute,
//            @RequestParam(name = "sortField", required = false, defaultValue = "lastName") String sortField,
//            @RequestParam(name = "sortDir", required = false, defaultValue = "asc") String sortDir,
//            HttpServletResponse response) throws IOException {
//
//        Sort sort = sortDir.equalsIgnoreCase("desc")
//                ? Sort.by(sortField).descending()
//                : Sort.by(sortField).ascending();
//
//        Specification<Student> spec = StudentSpecifications.withFilters(
//                applicationYear, groupName, course, specialtyTitle, institute, null
//        ).and((root, query, cb) -> cb.equal(root.get("status"), StudentStatus.APPLIED));
//
//        List<Student> students = studentRepository.findAll(spec, sort);
//
//        List<StudentCredentialsRow> rows = students.stream()
//                .map(s -> new StudentCredentialsRow(
//                        s.getLastName(),
//                        s.getFirstName(),
//                        s.getPatronymic(),
//                        s.getUser() != null ? s.getUser().getLogin() : "",
//                        "" // raw password из БД не получить, если он не хранится отдельно
//                ))
//                .toList();
//
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setHeader("Content-Disposition", "attachment; filename=student-credentials.xlsx");
//
//        ExcelExportUtil.exportCredentials(rows, response.getOutputStream());
//    }

//    @GetMapping("/student-applied/export-credentials")
//    public void exportCredentials(
//            @RequestParam(name = "applicationYear", required = false) Integer applicationYear,
//            @RequestParam(name = "group", required = false) String groupName,
//            @RequestParam(name = "course", required = false) Integer course,
//            @RequestParam(name = "specialty", required = false) String specialtyTitle,
//            @RequestParam(name = "institute", required = false) String institute,
//            @RequestParam(name = "sortField", required = false, defaultValue = "lastName") String sortField,
//            @RequestParam(name = "sortDir", required = false, defaultValue = "asc") String sortDir,
//            HttpServletResponse response) throws IOException {
//
//        Sort sort = sortDir.equalsIgnoreCase("desc")
//                ? Sort.by(sortField).descending()
//                : Sort.by(sortField).ascending();
//
//        Specification<Student> spec = StudentSpecifications.withFilters(
//                applicationYear, groupName, course, specialtyTitle, institute, null
//        ).and((root, query, cb) -> cb.equal(root.get("status"), StudentStatus.APPLIED));
//
//        List<Student> students = studentRepository.findAll(spec, sort);
//
//        List<StudentCredentialsRow> rows = students.stream()
//                .map(s -> {
//                    StudentCredentials creds = studentCredentialsRepository
//                            .findByStudent_IdStudent(s.getIdStudent())
//                            .orElse(null);
//
//                    return new StudentCredentialsRow(
//                            s.getLastName(),
//                            s.getFirstName(),
//                            s.getPatronymic(),
//                            creds != null ? creds.getRawPassword() : ""
//                    );
//                })
//                .toList();
//
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setHeader("Content-Disposition", "attachment; filename=student-credentials.xlsx");
//
//        ExcelExportUtil.exportCredentials(rows, response.getOutputStream());
//    }

    @GetMapping("/student-applied/export-credentials")
    public void exportCredentials(
            @RequestParam(name = "applicationYear", required = false) Integer applicationYear,
            @RequestParam(name = "group", required = false) String groupName,
            @RequestParam(name = "course", required = false) Integer course,
            @RequestParam(name = "specialty", required = false) String specialtyTitle,
            @RequestParam(name = "institute", required = false) String institute,
            @RequestParam(name = "sortField", required = false, defaultValue = "lastName") String sortField,
            @RequestParam(name = "sortDir", required = false, defaultValue = "asc") String sortDir,
            HttpServletResponse response) throws IOException {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();

        Specification<Student> spec = StudentSpecifications.withFilters(
                applicationYear, groupName, course, specialtyTitle, institute, null
        ).and((root, query, cb) -> cb.equal(root.get("status"), StudentStatus.APPLIED));

        List<Student> students = studentRepository.findAll(spec, sort);
        List<StudentCredentialsRow> rows = studentService.getCredentialsRowsAndDelete(students);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=student-credentials.xlsx");

        ExcelExportUtil.exportCredentials(rows, response.getOutputStream());
    }

    // Детальная страница студента
    @GetMapping("/student-applied/{id}")
    public String studentDetails(@PathVariable("id") long id, Model model) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Неверный идентификатор студента: " + id));
        model.addAttribute("student", student);
        return "student-details";
    }

    /* ===================== РЕДАКТИРОВАНИЕ ===================== */

    @GetMapping("/student-applied/{id}/edit")
    public String studentEdit(@PathVariable("id") long idStudent, Model model) {
        Student student = studentRepository.findById(idStudent).orElseThrow();

        if (student.getPassport() == null) {
            student.setPassport(new Passport());
        }

        model.addAttribute("student", student);
        model.addAttribute("groups",
                studyGroupRepository.findAll(Sort.by("nameGroup")));
        model.addAttribute("vusList",
                militaryAccountingSpecialtyEntityRepository.findAll(Sort.by("code")));
        model.addAttribute("commissariats",
                militaryCommissariatRepository.findAll(Sort.by("name")));

        return "student-edit";
    }
    @Transactional
    @PostMapping("/student-applied/{id}/edit")
    public String studentUpdate(@PathVariable("id") long idStudent,
                                @ModelAttribute Student formStudent) {

        Student existingStudent = studentRepository.findById(idStudent).orElseThrow();

        existingStudent.setFirstName(formStudent.getFirstName());
        existingStudent.setLastName(formStudent.getLastName());
        existingStudent.setPatronymic(formStudent.getPatronymic());
        existingStudent.setBirthday(formStudent.getBirthday());

        existingStudent.setGroupStudent(formStudent.getGroupStudent());
        if (formStudent.getGroupStudent() != null) {
            existingStudent.setCourse(formStudent.getGroupStudent().getCourse());
        } else {
            existingStudent.setCourse(formStudent.getCourse());
        }

        existingStudent.setMilitaryCommissariat(formStudent.getMilitaryCommissariat());
        existingStudent.setMilitaryAccountingSpecialty(formStudent.getMilitaryAccountingSpecialty());
        existingStudent.setStudentIdCard(formStudent.getStudentIdCard());
        existingStudent.setPhoneNumber(formStudent.getPhoneNumber());
        existingStudent.setNoteStudent(formStudent.getNoteStudent());

        // === ПАСПОРТ ===
        Passport formPassport = formStudent.getPassport();
        Passport existingPassport = existingStudent.getPassport();

        boolean formPassportEmpty = (formPassport == null) ||
                ((formPassport.getNumberPassport() == null || formPassport.getNumberPassport().isBlank()) &&
                        (formPassport.getSeriesPassport() == null || formPassport.getSeriesPassport().isBlank()) &&
                        formPassport.getDatePassport() == null &&
                        (formPassport.getPlacePassport() == null || formPassport.getPlacePassport().isBlank()));

        if (formPassportEmpty) {
            // если в форме всё пусто — удаляем паспорт, если он был
            existingStudent.setPassport(null); // из-за orphanRemoval паспорт тоже удалится
        } else {
            if (existingPassport == null) {
                existingPassport = new Passport();
                existingPassport.setStudent(existingStudent);
                existingStudent.setPassport(existingPassport);
            }

            existingPassport.setNumberPassport(formPassport.getNumberPassport());
            existingPassport.setSeriesPassport(formPassport.getSeriesPassport());
            existingPassport.setPlacePassport(formPassport.getPlacePassport());
            existingPassport.setDatePassport(formPassport.getDatePassport());
        }

        studentStatusService.recalculateStatus(existingStudent);

        studentRepository.save(existingStudent);
        //return "redirect:/student-applied/" + idStudent;
        return "redirect:/students/" + idStudent + "/portfolio";
    }

    /* ===================== УДАЛЕНИЕ ===================== */

    @Transactional
    @PostMapping("/student-applied/{id}/delete")
    public String studentDelete(@PathVariable("id") long idStudent) {
        Student student = studentRepository.findById(idStudent).orElseThrow();
        studentRepository.delete(student);
        return "redirect:/student-applied";
    }

    /* ===================== ПЕРЕВОД В КУРСАНТЫ / ВЗВОД ===================== */

    @PostMapping("/student-applied/{id}/approve")
    public String approveStudent(@PathVariable("id") Long id) {
        Student student = studentRepository.findById(id).orElseThrow();

        student.setStatus(StudentStatus.CADET);

        List<Platoon> platoons = platoonRepository.findByNamePlatoon(String.valueOf(student.getCourse()));
        if (platoons.isEmpty()) {
            throw new IllegalStateException("Нет взводов для курса " + student.getCourse());
        }

        Platoon targetPlatoon = platoons.get(0);
        student.setPlatoon(targetPlatoon);

        studentRepository.save(student);
        return "redirect:/student-applied/" + id;
    }

    /* ===================== ФИЗИЧЕСКАЯ ПОДГОТОВКА ===================== */

    // Закомментированные методы можно вернуть позже, адаптировав сортировку под groupStudent.specialty

    private String calculateFinalResult(int totalPoints) {
        if (totalPoints >= 235) return "100";
        if (totalPoints >= 220) return "95";
        if (totalPoints >= 200) return "85";
        if (totalPoints >= 180) return "75";
        if (totalPoints >= 160) return "65";
        return "незачёт";
    }

    @GetMapping("/students")
    public String allStudentsList(
            @RequestParam(name = "sortField", required = false, defaultValue = "lastName") String sortField,
            @RequestParam(name = "sortDir", required = false, defaultValue = "asc") String sortDir,

            @RequestParam(name = "applicationYear", required = false) Integer applicationYear,
            @RequestParam(name = "groupName", required = false) String groupName,
            @RequestParam(name = "course", required = false) Integer course,
            @RequestParam(name = "specialtyTitle", required = false) String specialtyTitle,
            @RequestParam(name = "institute", required = false) String institute,
            @RequestParam(name = "platoonId", required = false) Long platoonId,
            @RequestParam(name = "status", required = false) StudentStatus status,

            Model model) {

        // ✅ ПРАВИЛЬНАЯ сортировка (строковые константы!)
        Sort sort;
        if (sortDir.equalsIgnoreCase("desc")) {
            switch (sortField) {
                case "lastName" -> sort = Sort.by("lastName").descending();
                case "course" -> sort = Sort.by("course").descending();
                case "age" -> sort = Sort.by("birthday").descending();  // через birthday
                default -> sort = Sort.by("lastName").descending();
            }
        } else {
            switch (sortField) {
                case "lastName" -> sort = Sort.by("lastName").ascending();
                case "course" -> sort = Sort.by("course").ascending();
                case "age" -> sort = Sort.by("birthday").ascending();
                default -> sort = Sort.by("lastName").ascending();
            }
        }

        // ✅ Фильтрация
        Specification<Student> spec = StudentSpecifications.withFilters(
                applicationYear, groupName, course, specialtyTitle, institute, null
        );

        if (platoonId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("platoon").get("id"), platoonId));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), status));
        }

        List<Student> students = studentRepository.findAll(spec, sort);

        // Данные для фильтров (добавь недостающие в контроллер)
        model.addAttribute("applicationYears", studentRepository.findDistinctApplicationYears());
        model.addAttribute("groups", studyGroupRepository.findAll(Sort.by("nameGroup")));
        model.addAttribute("courses", List.of(1, 2, 3, 4, 5));
        model.addAttribute("institutes", specialtyCodeInstituteRepository.findDistinctInstitute());
        model.addAttribute("selectedInstitute", institute);
        model.addAttribute("platoons", platoonRepository.findAll(Sort.by("namePlatoon")));
        model.addAttribute("statuses", List.of(StudentStatus.values()));  // или из БД

        // Сохранение параметров
        model.addAttribute("selectedApplicationYear", applicationYear);
        model.addAttribute("selectedGroupName", groupName);
        model.addAttribute("selectedCourse", course);
        model.addAttribute("selectedSpecialtyTitle", specialtyTitle);
        model.addAttribute("selectedInstitute", institute);
        model.addAttribute("selectedPlatoonId", platoonId);
        model.addAttribute("selectedStatus", status);

        model.addAttribute("students", students);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "students/list";
    }
}