package ru.accouting.student.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.accouting.student.config.SecurityUtils;
import ru.accouting.student.model.MilitaryUnit;
import ru.accouting.student.repository.MilitaryUnitRepository;

import java.util.List;

@RestController
@RequestMapping("/api/military-units")
@RequiredArgsConstructor
public class MilitaryUnitController {

    private final MilitaryUnitRepository militaryUnitRepository;
    private final SecurityUtils securityUtils;

    /**
     * Получить список всех воинских частей (доступно всем авторизованным).
     */
    @GetMapping
    public List<MilitaryUnit> getAllMilitaryUnits() {
        return militaryUnitRepository.findAll();
    }

    /**
     * Добавить новую воинскую часть (только администратор).
     * Тело запроса: строка с названием части (Content-Type: text/plain).
     */
    @PostMapping
    public ResponseEntity<Object> createMilitaryUnit(@RequestBody String name) {
        if (!securityUtils.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String trimmed = name.trim();
        if (militaryUnitRepository.findByName(trimmed).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Воинская часть с названием \"" + trimmed + "\" уже существует");
        }

        MilitaryUnit unit = new MilitaryUnit();
        unit.setName(trimmed);
        militaryUnitRepository.save(unit);
        return ResponseEntity.status(HttpStatus.CREATED).body(unit);
    }
}