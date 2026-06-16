// StudentOrderRepository.java
package ru.accouting.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.accouting.student.model.StudentOrder;
import java.util.List;

public interface StudentOrderRepository extends JpaRepository<StudentOrder, Long> {
    List<StudentOrder> findAllByStudent_IdStudent(Long studentId);
    void deleteByStudent_IdStudentAndOrder_Id(Long studentId, Long orderId);
}