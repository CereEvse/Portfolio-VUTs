// OrderRepository.java
package ru.accouting.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.accouting.student.model.Order;
import ru.accouting.student.model.OrderType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumberAndOrderDateAndOrderType(
            String orderNumber, LocalDate orderDate, OrderType orderType);
    List<Order> findByOrderType(OrderType orderType);
}