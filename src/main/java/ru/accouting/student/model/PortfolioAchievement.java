package ru.accouting.student.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.accouting.student.model.Student;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio_achievement")
@Getter
@Setter
@ToString(exclude = "student")
public class PortfolioAchievement {

    @Id
    @Column(name="id_portfolio")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "portfolio_achievement_seq")
    @SequenceGenerator(name = "portfolio_achievement_seq", sequenceName = "portfolio_achievement_seq", allocationSize = 1)
    private Long idPortfolio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_student", nullable = false)
    private Student student;

    @Column(name = "achievement_date", nullable = false)
    private LocalDate achievementDate;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "file_path")
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AchievementType type;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}