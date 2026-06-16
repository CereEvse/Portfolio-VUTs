package ru.accouting.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.accouting.student.model.PortfolioAchievement;

import java.util.List;

@Repository
public interface PortfolioAchievementRepository extends JpaRepository<PortfolioAchievement, Long> {

    List<PortfolioAchievement> findAllByStudentIdStudentOrderByAchievementDateDesc(Long studentId);
}
