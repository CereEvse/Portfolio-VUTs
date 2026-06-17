# Военный учебный центр – Система портфолио студентов, проходящих военную подготовку

Веб-приложение для автоматизации учёта достижений студентов, проходящих военную подготовку в Военном учебном центре (ВУЦ).  
Включает личные карточки, портфолио достижений, результаты физической подготовки, военно‑учётные данные и разграничение прав доступа.

---

## Основные возможности

- **Личная карточка студента**  
  Фото, персональные и учебные данные, статус обучения.

- **Портфолио достижений**  
  Достижения четырёх типов: учебные, спортивные, научные, иные.  
  Для каждого достижения указываются дата, описание и прикреплённый файл.

- **Оценки по военным модулям** (5‑балльная система)  
  Модули: военно‑техническая, тактическая, общевоенная подготовка, учебные сборы, итоговая аттестация.

- **Результаты физической подготовки**  
  Сила, быстрота, выносливость: номер упражнения, результат, баллы.

- **Военно‑учётные данные**  
  Приказы о допуске и присвоении звания, принятие присяги, учебные сборы.  

- **Ролевая модель**  
  Администратор и сотрудник ВУЦ видят и редактируют данные всех студентов.  
  Студент видит только свою страницу.

- **Загрузка и просмотр файлов**  
  Фотографии студентов и файлы достижений сохраняются на сервере.

---

## Технологический стек

| Слой           | Технологии                                                            |
|----------------|-----------------------------------------------------------------------|
| Backend        | Java 21, Spring Boot 3.5.x, Spring Data JPA, Spring Security         |
| Frontend       | Thymeleaf, Bootstrap 5, Bootstrap Icons,                             |
| База данных    | PostgreSQL (основная),                                               |
| Миграции       | Liquibase                                                             |
| Сборка         | Gradle                                                                |
| Утилиты        | Lombok, Jackson, Spring Validation                                   |

---

## Структура проекта (ключевые пакеты)

```
ru.accouting
├── config              – конфигурации (Spring Security и пр.)
├── portfolio
│   ├── controller      – REST-контроллеры портфолио
│   ├── dto             – DTO для достижений, военных данных
│   ├── model           – PortfolioAchievement, AchievementType
│   ├── repository      – PortfolioAchievementRepository
│   └── service         – PortfolioService
├── security            – SecurityUtils, CustomUserDetails
└── student
    ├── controller      – PortfolioPageController, MilitaryDetailsController, MilitaryUnitController, StudentPhotoController
    ├── dto             – MilitaryGradesDto, OrderDto, OathDto, TrainingCampPeriodDto
    ├── model           – Student, PhysicalTraining, Exercise, MilitaryUnit, Order, MilitaryOath, TrainingCampPeriod, StudentMilitaryGrades и связующие сущности
    ├── repository      – JPA-репозитории
    └── service         – MilitaryDetailsService, StudentPhotoService, MilitaryGradesService
```

---

## Установка и запуск (локально)

1. **Требования**  
   - JDK 21  
   - PostgreSQL (можно заменить на H2 для тестов)

2. **Клонировать репозиторий**  
   ```bash
   git clone <url>
   cd <project-folder>
   ```

3. **Настроить подключение к БД**  
   Отредактировать `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/vuc
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   ```
   Для H2 (встроенная, не требует установки):
   ```properties
   spring.datasource.url=jdbc:h2:mem:vuc
   spring.datasource.driver-class-name=org.h2.Driver
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
   ```

4. **Собрать и запустить**  
   ```bash
   ./gradlew bootRun
   ```

5. **Открыть приложение**  
   По умолчанию: `http://localhost:8080`.

---

## Настройка безопасности и пользователей

В системе используются роли `ADMIN`, `TECHNOLOGIST`, `STUDENT`  и `USER`.  
Связь учётной записи пользователя со студентом осуществляется через поле `user_id` в таблице `student`.  

Для тестирования можно настроить `UserDetailsService` и создать пользователей в базе данных или через `CommandLineRunner`.  
*Пример тестовых учётных данных (логин/пароль):*
- Администратор: `admin` / `admin`
- Студент: `student1` / `student1` (привязан к студенту с `id_student = 1`)

---

## API (основные эндпоинты)

### Портфолио
`GET    /api/students/{id}/achievements`  
`POST   /api/students/{id}/achievements` (multipart)  
`PUT    /api/achievements/{id}`  
`DELETE /api/achievements/{id}`  
`GET    /api/achievements/{id}/file`

### Оценки
`GET  /api/students/{id}/military-grades`  
`PUT  /api/students/{id}/military-grades`

### Военные данные
`GET  /api/students/{id}/military-details`  
`PUT  /api/students/{id}/military-details/admission-order`  
`PUT  …/rank-assignment-order`  
`PUT  …/oath`  
`PUT  …/training-camps`  
а также варианты с выбором существующих записей (`…/existing`)

### Воинские части
`GET   /api/military-units`  
`POST  /api/military-units`

### Фотография
`GET    /api/students/{id}/photo`  
`POST   /api/students/{id}/photo`  
`DELETE /api/students/{id}/photo`

Все операции записи доступны только администратору и сотруднику ВУЦ. Студент может читать только свои данные.

---

## Разработка и расширение

- Новые типы достижений добавляются в enum `AchievementType`.
- Для добавления полей в личную карточку студента изменяется сущность `Student`.
- Фронтенд реализован на Thymeleaf; страницы портфолио находятся в `templates/`.
- Миграции Liquibase лежат в `src/main/resources/db/changelog/`.
