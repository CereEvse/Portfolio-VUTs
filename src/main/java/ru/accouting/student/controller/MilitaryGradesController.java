package ru.accouting.student.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.accouting.student.config.SecurityUtils;
import ru.accouting.student.dto.MilitaryGradesDto;
import ru.accouting.student.service.MilitaryGradesService;

@RestController
@RequestMapping("/api/students/{studentId}/military-grades")
@RequiredArgsConstructor
public class MilitaryGradesController {

    private final MilitaryGradesService militaryGradesService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public MilitaryGradesDto getGrades(@PathVariable Long studentId) {
        return militaryGradesService.getGrades(studentId);
    }

    @PutMapping
    public ResponseEntity<?> saveGrades(@PathVariable Long studentId,
                                        @Valid @RequestBody MilitaryGradesDto dto) {
        if (!securityUtils.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        militaryGradesService.saveGrades(studentId, dto);
        return ResponseEntity.ok().build();
    }
}