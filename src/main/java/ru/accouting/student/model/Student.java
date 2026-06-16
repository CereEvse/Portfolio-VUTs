package ru.accouting.student.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@ToString
@Entity(name="student")
@Table(name="student")
@Getter
@Setter
public class Student {

    @Id
    @Column(name="id_student")
    @GeneratedValue(generator = "id_student_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "id_student_seq", sequenceName = "id_student_seq", initialValue = 1, allocationSize = 1)
    private Long idStudent;

    @Column(name = "id_user") // для портфолио
    private Long idUser;

    @Column(name = "photo_path") // для портфолио
    private String photoPath;

    @Column(name="firstName")
    private String firstName;

    @Column(name="lastName")
    private String lastName;

    @Column(name="patronymic")
    private String patronymic;

    // дата рождения как LocalDate
    @Column(name="birthday")
    private LocalDate birthday;

    @Column(name="course")
    private Integer course;

    @ManyToOne
    @JoinColumn(name = "study_group_id")
    private StudyGroup groupStudent;

    @ManyToOne
    @JoinColumn(name = "military_commissariat_id")
    private MilitaryCommissariatEntity militaryCommissariat;

    @ManyToOne
    @JoinColumn(name = "military_accounting_specialty_id")
    private MilitaryAccountingSpecialtyEntity militaryAccountingSpecialty;

    @Column(name="studentIdCard")
    private String studentIdCard;

    @Column(name="phoneNumber")
    private String phoneNumber;

    @Column(name = "noteStudent", length = 255)
    private String noteStudent;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "passport", unique = true)
    private Passport passport;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StudentStatus status;

    @ManyToOne
    @JoinColumn(name = "platoon_id")
    private Platoon platoon;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private PhysicalTraining physicalTraining;

    @Enumerated(EnumType.STRING)
    private FitnessCategory fitnessCategory;

    @Enumerated(EnumType.STRING)
    private PsychoCategory psychoCategory;

    @Column(precision = 3, scale = 2)
    private BigDecimal grade5;

    @Column(precision = 5, scale = 2)
    private BigDecimal grade100;

    @Column(name = "application_year")
    private Integer applicationYear;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // Геттеры для отображения в HTML
    // если тебе нужно отображать результаты ФП, можно прокидывать их через physicalTraining
    @Transient
    public String getStrengthResultDisplay() {
        if (physicalTraining == null || physicalTraining.getStrengthResult() == null) {
            return "";
        }
        return physicalTraining.getStrengthResult().toString();
    }

    @Transient
    public String getSpeedResultDisplay() {
        if (physicalTraining == null || physicalTraining.getSpeedResult() == null) {
            return "";
        }
        return String.format("%.2f", physicalTraining.getSpeedResult()).replace('.', ',');
    }

    @Transient
    public String getEnduranceResultDisplay() {
        if (physicalTraining == null || physicalTraining.getEnduranceResult() == null) {
            return "";
        }
        return String.format("%.2f", physicalTraining.getEnduranceResult()).replace('.', ',');
    }


    // ---------- ВОЗРАСТ ----------

    @Transient
    public Integer getAge() {
        if (birthday == null) {
            return null;
        }
        return Period.between(birthday, LocalDate.now()).getYears();
    }

    @Transient
    public boolean isUnder25() {
        Integer age = getAge();
        return age != null && age < 25;
    }

    @Transient
    public boolean isOverOrEqual25() {
        Integer age = getAge();
        return age != null && age >= 25;
    }

    public enum FitnessCategory {
        A, B, C, D, E
    }

    public enum PsychoCategory {
        I, II, III, IV
    }

    // ---------- конструкторы ----------

    public Student() {
    }

    public Student(String firstname, String lastName, String email) {
        this.firstName = firstname;
        this.lastName = lastName;
        //this.email = email;
    }
}
