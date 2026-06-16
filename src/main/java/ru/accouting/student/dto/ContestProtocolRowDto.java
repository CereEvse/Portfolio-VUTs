package ru.accouting.student.dto;

import lombok.Getter;
import lombok.Setter;
import ru.accouting.student.model.MilitaryAccountingSpecialtyEntity;
import ru.accouting.student.model.StudentStatus;

@Getter
@Setter
public class ContestProtocolRowDto {

    private Long studentId;

    private String fullName;
    private String birthDate;            // строкой dd.MM.yyyy
    private String specialtyCode;        // код специальности
    private String groupName;
    private Integer course;

    // результаты предварительного отбора
    private String fitnessCategory;      // Категория годности (А, Б …)
    private String psychoCategory;       // I / II / III / IV

    private String ageGroup;             // возрастная группа (I, II, III...)

    // физподготовка: три упражнения
    private Integer strengthExerciseNumber;
    private String strengthResult;       // результат (формат как у тебя)
    private Integer strengthPoints;

    private Integer speedExerciseNumber;
    private String speedResult;
    private Integer speedPoints;

    private Integer enduranceExerciseNumber;
    private String enduranceResult;
    private Integer endurancePoints;

    private Integer totalPoints;         // общий балл (по правилам – сумма нужных компонентов)

    private String physicalRequirementsMatch; // «соответствует / не соответствует»
    private Integer physical100;         // оценка уровня физ. подготовки по 100‑балльной шкале

    // льготы
    private boolean hasQuotaRight;       // право допуска ≥10% (квота)
    private boolean hasPriorityRight;    // преимущественное право допуска

    private Integer academic100;         // оценка текущей успеваемости (100‑балльная)
    private Integer finalScore;          // итоговый результат (физика + учёба + льготы и т.п.)
    private String commissionDecision;   // решение комиссии

    private StudentStatus status;
    private MilitaryAccountingSpecialtyEntity militaryAccountingSpecialty;

    private Long platoonId;
    private String platoonName;
}
