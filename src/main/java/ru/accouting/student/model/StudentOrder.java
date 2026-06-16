package ru.accouting.student.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "student_order",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "order_id"}))
@Getter
@Setter
public class StudentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_order_seq")
    @SequenceGenerator(name = "student_order_seq", sequenceName = "student_order_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}