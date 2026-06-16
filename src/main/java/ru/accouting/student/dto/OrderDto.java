package ru.accouting.student.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class OrderDto {
    private Long id;
    private String orderNumber;
    private LocalDate orderDate;
    private String issuedBy;
    private String orderType; // "ADMISSION" или "RANK_ASSIGNMENT"
}