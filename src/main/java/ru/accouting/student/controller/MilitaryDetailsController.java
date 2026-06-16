package ru.accouting.student.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import ru.accouting.student.config.SecurityUtils;
import ru.accouting.student.dto.MilitaryDetailsDto;
import ru.accouting.student.dto.OathDto;
import ru.accouting.student.dto.OrderDto;
import ru.accouting.student.dto.TrainingCampPeriodDto;
import ru.accouting.student.model.*;
import ru.accouting.student.repository.StudentRepository;
import ru.accouting.student.service.MilitaryDetailsService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students/{studentId}/military-details")
@RequiredArgsConstructor
public class MilitaryDetailsController {

    private final MilitaryDetailsService militaryDetailsService;
    private final SecurityUtils securityUtils;
    private final StudentRepository studentRepository;

    /**
     * Проверка доступа к данным студента.
     * Доступно только администратору или самому студенту (если его userId совпадает с userId студента),
     * и только если студент имеет статус CADET.
     */
    private void checkAccess(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));

        //Портфолио только у курсантов
//        if (student.getStatus() != StudentStatus.CADET) {
//            throw new AccessDeniedException("Данные доступны только для курсантов");
//        }

        if (!securityUtils.isAdmin()) {
            Long currentUserId = securityUtils.getCurrentUserId()
                    .orElseThrow(() -> new AccessDeniedException("Пользователь не авторизован"));
            if (!currentUserId.equals(student.getIdUser())) {
                throw new AccessDeniedException("Доступ запрещён");
            }
        }
    }

    // ==================== ПОЛУЧЕНИЕ ВСЕХ ВОЕННЫХ ДАННЫХ СТУДЕНТА ====================

    @GetMapping
    public MilitaryDetailsDto getMilitaryDetails(@PathVariable Long studentId) {
        checkAccess(studentId);
        return militaryDetailsService.getMilitaryDetails(studentId);
    }

    // ==================== ПРИКАЗ О ДОПУСКЕ К ВОЕННОЙ ПОДГОТОВКЕ ====================

    /**
     * Обновить приказ о допуске (создать новый или привязать существующий).
     * Параметры: orderNumber, orderDate (обязательные), issuedBy (необязательный, только для приказа о присвоении звания).
     */
    @PutMapping("/admission-order")
    public ResponseEntity<?> setAdmissionOrder(
            @PathVariable Long studentId,
            @RequestParam String orderNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate orderDate,
            @RequestParam(required = false) String issuedBy) {
        if (!securityUtils.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        checkAccess(studentId);
        militaryDetailsService.setAdmissionOrder(studentId, orderNumber, orderDate, issuedBy);
        return ResponseEntity.ok().build();
    }

    /**
     * Привязать существующий приказ о допуске по его ID.
     */
    @PutMapping("/admission-order/existing")
    public ResponseEntity<?> setExistingAdmissionOrder(
            @PathVariable Long studentId,
            @RequestParam Long orderId) {
        if (!securityUtils.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        checkAccess(studentId);
        // В сервисе нужно добавить метод setExistingAdmissionOrder
        militaryDetailsService.setExistingAdmissionOrder(studentId, orderId);
        return ResponseEntity.ok().build();
    }

    /**
     * Получить список всех приказов о допуске (для выбора в интерфейсе).
     */
    @GetMapping("/admission-orders")
    public List<OrderDto> getAdmissionOrders() {
        return militaryDetailsService.getOrdersByType(OrderType.ADMISSION)
                .stream()
                .map(this::toOrderDto)
                .collect(Collectors.toList());
    }

    // ==================== ПРИКАЗ О ПРИСВОЕНИИ ВОИНСКОГО ЗВАНИЯ ====================

    @PutMapping("/rank-assignment-order")
    public ResponseEntity<?> setRankAssignmentOrder(
            @PathVariable Long studentId,
            @RequestParam String orderNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate orderDate,
            @RequestParam String issuedBy) {
        if (!securityUtils.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        checkAccess(studentId);
        militaryDetailsService.setRankAssignmentOrder(studentId, orderNumber, orderDate, issuedBy);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/rank-assignment-order/existing")
    public ResponseEntity<?> setExistingRankAssignmentOrder(
            @PathVariable Long studentId,
            @RequestParam Long orderId) {
        if (!securityUtils.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        checkAccess(studentId);
        militaryDetailsService.setExistingRankAssignmentOrder(studentId, orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rank-assignment-orders")
    public List<OrderDto> getRankAssignmentOrders() {
        return militaryDetailsService.getOrdersByType(OrderType.RANK_ASSIGNMENT)
                .stream()
                .map(this::toOrderDto)
                .collect(Collectors.toList());
    }

    // ==================== ПРИСЯГА ====================

    /**
     * Создать/привязать новую запись о присяге.
     * Параметры: oathDate, militaryUnitId.
     */
    @PutMapping("/oath")
    public ResponseEntity<?> saveOath(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate oathDate,
            @RequestParam Long militaryUnitId) {
        if (!securityUtils.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        checkAccess(studentId);
        militaryDetailsService.saveOath(studentId, oathDate, militaryUnitId);
        return ResponseEntity.ok().build();
    }

    /**
     * Привязать существующую присягу по ID.
     */
    @PutMapping("/oath/existing")
    public ResponseEntity<?> setExistingOath(
            @PathVariable Long studentId,
            @RequestParam Long oathId) {
        if (!securityUtils.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        checkAccess(studentId);
        militaryDetailsService.setExistingOath(studentId, oathId);
        return ResponseEntity.ok().build();
    }

    /**
     * Получить список всех записей о присяге (для выбора).
     */
    @GetMapping("/oaths")
    public List<OathDto> getAllOaths() {
        return militaryDetailsService.getAllOaths()
                .stream()
                .map(this::toOathDto)
                .collect(Collectors.toList());
    }

    // ==================== УЧЕБНЫЕ СБОРЫ ====================

    /**
     * Установить период учебных сборов (создать или привязать существующий).
     */
    @PutMapping("/training-camps")
    public ResponseEntity<?> saveTrainingCamps(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (!securityUtils.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        checkAccess(studentId);
        militaryDetailsService.saveTrainingCamps(studentId, startDate, endDate);
        return ResponseEntity.ok().build();
    }

    /**
     * Привязать существующий период учебных сборов по ID.
     */
    @PutMapping("/training-camps/existing")
    public ResponseEntity<?> setExistingTrainingCamp(
            @PathVariable Long studentId,
            @RequestParam Long periodId) {
        if (!securityUtils.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        checkAccess(studentId);
        militaryDetailsService.setExistingTrainingCamp(studentId, periodId);
        return ResponseEntity.ok().build();
    }

    /**
     * Получить список всех периодов учебных сборов (для выбора).
     */
    @GetMapping("/training-camp-periods")
    public List<TrainingCampPeriodDto> getAllTrainingCampPeriods() {
        return militaryDetailsService.getAllTrainingCampPeriods()
                .stream()
                .map(this::toTrainingCampPeriodDto)
                .collect(Collectors.toList());
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ПРЕОБРАЗОВАНИЯ ====================

    private OrderDto toOrderDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setOrderDate(order.getOrderDate());
        dto.setIssuedBy(order.getIssuedBy());
        dto.setOrderType(order.getOrderType().name());
        return dto;
    }

    private OathDto toOathDto(MilitaryOath oath) {
        OathDto dto = new OathDto();
        dto.setId(oath.getId());
        dto.setOathDate(oath.getOathDate());
        dto.setMilitaryUnitName(oath.getMilitaryUnit().getName());
        dto.setMilitaryUnitId(oath.getMilitaryUnit().getId());
        return dto;
    }

    private TrainingCampPeriodDto toTrainingCampPeriodDto(TrainingCampPeriod period) {
        TrainingCampPeriodDto dto = new TrainingCampPeriodDto();
        dto.setId(period.getId());
        dto.setStartDate(period.getStartDate());
        dto.setEndDate(period.getEndDate());
        return dto;
    }
}