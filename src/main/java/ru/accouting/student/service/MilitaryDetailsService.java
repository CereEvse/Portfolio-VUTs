package ru.accouting.student.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.accouting.student.dto.*;
import ru.accouting.student.model.*;
import ru.accouting.student.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MilitaryDetailsService {

    private final StudentRepository studentRepository;
    private final OrderRepository orderRepository;
    private final MilitaryUnitRepository militaryUnitRepository;
    private final MilitaryOathRepository militaryOathRepository;
    private final TrainingCampPeriodRepository trainingCampPeriodRepository;
    private final StudentOrderRepository studentOrderRepository;
    private final StudentOathRepository studentOathRepository;
    private final StudentTrainingCampPeriodRepository studentTrainingCampPeriodRepository;

    // ==================== ПОЛУЧЕНИЕ ВСЕХ ВОЕННЫХ ДАННЫХ СТУДЕНТА ====================

    @Transactional(readOnly = true)
    public MilitaryDetailsDto getMilitaryDetails(Long studentId) {
        List<StudentOrder> studentOrders = studentOrderRepository.findAllByStudent_IdStudent(studentId);

        OrderDto admissionOrder = null;
        OrderDto rankOrder = null;
        for (StudentOrder so : studentOrders) {
            Order order = so.getOrder();
            if (order.getOrderType() == OrderType.ADMISSION) {
                admissionOrder = toOrderDto(order);
            } else if (order.getOrderType() == OrderType.RANK_ASSIGNMENT) {
                rankOrder = toOrderDto(order);
            }
        }

        OathDto oathDto = studentOathRepository.findByStudent_IdStudent(studentId)
                .map(so -> toOathDto(so.getOath()))
                .orElse(null);

        TrainingCampPeriodDto tcpDto = studentTrainingCampPeriodRepository.findByStudent_IdStudent(studentId)
                .map(stcp -> toTrainingCampPeriodDto(stcp.getTrainingCampPeriod()))
                .orElse(null);

        MilitaryDetailsDto dto = new MilitaryDetailsDto();
        dto.setAdmissionOrder(admissionOrder);
        dto.setRankAssignmentOrder(rankOrder);
        dto.setOath(oathDto);
        dto.setTrainingCampPeriod(tcpDto);
        return dto;
    }

    // ==================== ПРИКАЗ О ДОПУСКЕ ====================

    @Transactional
    public void setAdmissionOrder(Long studentId, String orderNumber, LocalDate orderDate, String issuedBy) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));

        Order order = orderRepository.findByOrderNumberAndOrderDateAndOrderType(orderNumber, orderDate, OrderType.ADMISSION)
                .orElseGet(() -> {
                    Order newOrder = new Order();
                    newOrder.setOrderNumber(orderNumber);
                    newOrder.setOrderDate(orderDate);
                    newOrder.setIssuedBy(issuedBy);
                    newOrder.setOrderType(OrderType.ADMISSION);
                    return orderRepository.save(newOrder);
                });

        deleteExistingStudentOrdersByType(studentId, OrderType.ADMISSION);

        StudentOrder so = new StudentOrder();
        so.setStudent(student);
        so.setOrder(order);
        studentOrderRepository.save(so);
    }

    @Transactional
    public void setExistingAdmissionOrder(Long studentId, Long orderId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Приказ не найден"));
        if (order.getOrderType() != OrderType.ADMISSION) {
            throw new RuntimeException("Приказ не является приказом о допуске");
        }

        deleteExistingStudentOrdersByType(studentId, OrderType.ADMISSION);

        StudentOrder so = new StudentOrder();
        so.setStudent(student);
        so.setOrder(order);
        studentOrderRepository.save(so);
    }

    // ==================== ПРИКАЗ О ПРИСВОЕНИИ ЗВАНИЯ ====================

    @Transactional
    public void setRankAssignmentOrder(Long studentId, String orderNumber, LocalDate orderDate, String issuedBy) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));

        Order order = orderRepository.findByOrderNumberAndOrderDateAndOrderType(orderNumber, orderDate, OrderType.RANK_ASSIGNMENT)
                .orElseGet(() -> {
                    Order newOrder = new Order();
                    newOrder.setOrderNumber(orderNumber);
                    newOrder.setOrderDate(orderDate);
                    newOrder.setIssuedBy(issuedBy);
                    newOrder.setOrderType(OrderType.RANK_ASSIGNMENT);
                    return orderRepository.save(newOrder);
                });

        deleteExistingStudentOrdersByType(studentId, OrderType.RANK_ASSIGNMENT);

        StudentOrder so = new StudentOrder();
        so.setStudent(student);
        so.setOrder(order);
        studentOrderRepository.save(so);
    }

    @Transactional
    public void setExistingRankAssignmentOrder(Long studentId, Long orderId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Приказ не найден"));
        if (order.getOrderType() != OrderType.RANK_ASSIGNMENT) {
            throw new RuntimeException("Приказ не является приказом о присвоении звания");
        }

        deleteExistingStudentOrdersByType(studentId, OrderType.RANK_ASSIGNMENT);

        StudentOrder so = new StudentOrder();
        so.setStudent(student);
        so.setOrder(order);
        studentOrderRepository.save(so);
    }

    // ==================== ПРИСЯГА ====================

    @Transactional
    public void saveOath(Long studentId, LocalDate oathDate, Long militaryUnitId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));
        MilitaryUnit unit = militaryUnitRepository.findById(militaryUnitId)
                .orElseThrow(() -> new RuntimeException("Воинская часть не найдена"));

        MilitaryOath oath = militaryOathRepository.findByOathDateAndMilitaryUnit_Id(oathDate, militaryUnitId)
                .orElseGet(() -> {
                    MilitaryOath newOath = new MilitaryOath();
                    newOath.setOathDate(oathDate);
                    newOath.setMilitaryUnit(unit);
                    return militaryOathRepository.save(newOath);
                });

        studentOathRepository.findByStudent_IdStudent(studentId).ifPresent(so -> {
            studentOathRepository.delete(so);
            studentOathRepository.flush(); // немедленное удаление
        });

        StudentOath so = new StudentOath();
        so.setStudent(student);
        so.setOath(oath);
        studentOathRepository.save(so);
    }

    @Transactional
    public void setExistingOath(Long studentId, Long oathId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));
        MilitaryOath oath = militaryOathRepository.findById(oathId)
                .orElseThrow(() -> new RuntimeException("Присяга не найдена"));

        studentOathRepository.findByStudent_IdStudent(studentId).ifPresent(so -> {
            studentOathRepository.delete(so);
            studentOathRepository.flush();
        });

        StudentOath so = new StudentOath();
        so.setStudent(student);
        so.setOath(oath);
        studentOathRepository.save(so);
    }

    // ==================== УЧЕБНЫЕ СБОРЫ ====================

    @Transactional
    public void saveTrainingCamps(Long studentId, LocalDate startDate, LocalDate endDate) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));

        TrainingCampPeriod period = trainingCampPeriodRepository.findByStartDateAndEndDate(startDate, endDate)
                .orElseGet(() -> {
                    TrainingCampPeriod newPeriod = new TrainingCampPeriod();
                    newPeriod.setStartDate(startDate);
                    newPeriod.setEndDate(endDate);
                    return trainingCampPeriodRepository.save(newPeriod);
                });

        studentTrainingCampPeriodRepository.findByStudent_IdStudent(studentId).ifPresent(stcp -> {
            studentTrainingCampPeriodRepository.delete(stcp);
            studentTrainingCampPeriodRepository.flush();
        });

        StudentTrainingCampPeriod stcp = new StudentTrainingCampPeriod();
        stcp.setStudent(student);
        stcp.setTrainingCampPeriod(period);
        studentTrainingCampPeriodRepository.save(stcp);
    }

    @Transactional
    public void setExistingTrainingCamp(Long studentId, Long periodId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));
        TrainingCampPeriod period = trainingCampPeriodRepository.findById(periodId)
                .orElseThrow(() -> new RuntimeException("Период сборов не найден"));

        studentTrainingCampPeriodRepository.findByStudent_IdStudent(studentId).ifPresent(stcp -> {
            studentTrainingCampPeriodRepository.delete(stcp);
            studentTrainingCampPeriodRepository.flush();
        });

        StudentTrainingCampPeriod stcp = new StudentTrainingCampPeriod();
        stcp.setStudent(student);
        stcp.setTrainingCampPeriod(period);
        studentTrainingCampPeriodRepository.save(stcp);
    }

    // ==================== СПРАВОЧНЫЕ МЕТОДЫ ====================

    @Transactional(readOnly = true)
    public List<Order> getOrdersByType(OrderType type) {
        return orderRepository.findByOrderType(type);
    }

    @Transactional(readOnly = true)
    public List<MilitaryOath> getAllOaths() {
        return militaryOathRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<TrainingCampPeriod> getAllTrainingCampPeriods() {
        return trainingCampPeriodRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<MilitaryUnit> getAllMilitaryUnits() {
        return militaryUnitRepository.findAll();
    }

    // ==================== ПРИВАТНЫЕ МЕТОДЫ ====================

    private void deleteExistingStudentOrdersByType(Long studentId, OrderType type) {
        List<StudentOrder> existing = studentOrderRepository.findAllByStudent_IdStudent(studentId);
        for (StudentOrder so : existing) {
            if (so.getOrder().getOrderType() == type) {
                studentOrderRepository.delete(so);
            }
        }
        studentOrderRepository.flush(); // немедленное удаление, чтобы избежать конфликта уникальности
    }

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