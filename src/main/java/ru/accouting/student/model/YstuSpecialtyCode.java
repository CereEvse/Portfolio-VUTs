package ru.accouting.student.model;

public enum YstuSpecialtyCode {
    MECHANICS_AND_MATHEMATICAL_MODELING("01.04.03", "Механика и математическое моделирование", "ИИМ"),
    FUNDAMENTAL_AND_APPLIED_CHEMISTRY("04.05.01", "Фундаментальная и прикладная химия (специалитет)", "ИХХТ"),
    CHEMICAL_TECHNOLOGY("18.03.01", "Химическая технология", "ИХХТ"),
    BIOTECHNOLOGY("19.03.01", "Биотехнология", "ИХХТ"),
    GROUND_TRANSPORTATION_AND_TECHNOLOGICAL_FACILITIES("23.05.01", "Наземные транспортно-технологические средства (специалитет)", "ИИМ"),
    MATERIALS_SCIENCE_AND_TECHNOLOGY_OF_MATERIALS("22.03.01", "Материаловедение и технологии материалов", "ИИМ"),
    TECHNOLOGICAL_MACHINES_AND_EQUIPMENT("15.03.02", "Технологические машины и оборудование", "ИИМ"),
    DESIGN_AND_TECHNOLOGICAL_SUPPORT_OF_MACHINE_BUILDING_INDUSTRIES("15.03.05", "Конструкторско-технологическое обеспечение машиностроительных производств", "ИИМ"),
    STANDARDIZATION_AND_METROLOGY("27.03.01","Стандартизация и метрология", "ИИМ"),
    SHIPBUILDING_OCEAN_ENGINEERING_AND_SYSTEM_ENGINEERING_OF_MARINE_INFRASTRUCTURE_FACILITIES("26.03.02", "Кораблестроение, океанотехника и системотехника объектов морской инфраструктуры", "ИИМ"),
    ENERGY_ENGINEERING("13.03.03", "Энергетическое машиностроение", "ИИМ"),
    MANAGEMENT_IN_TECHNICAL_SYSTEMS("27.03.04", "Управление в технических системах", "ИЦС"),
    INFORMATION_SYSTEMS_AND_TECHNOLOGIES("09.03.02", "Информационные системы и технологии", "ИЦС"),
    SOFTWARE_ENGINEERING("09.03.04", "Программная инженерия", "ИЦС"),
    INFOCOMMUNICATION_TECHNOLOGIES_AND_COMMUNICATION_SYSTEMS("11.03.02", "Инфокоммуникационные технологии и системы связи", "ИЦС"),
    INFORMATION_SECURITY("10.03.01", "Информационная безопасность", "ИЦС"),
    CONSTRUCTION("08.03.01", "Строительство", "ИИСТ"),
    APPLIED_GEODESY("21.05.01", "Прикладная геодезия (специалитет)", "ИИСТ"),
    ARCHITECTURE("07.03.01", "Архитектура", "ИАиД"),
    DESIGN("54.03.01", "Дизайн", "ИАиД"),
    QUALITY_MANAGEMENT("27.03.02", "Управление качеством", "ИЭМ"),
    PROFESSIONAL_TRAINING("44.03.04","Профессиональное обучение (по отраслям)","ИЭМ"),
    ECONOMY("38.03.01","Экономика", "ИЭМ"),
    MANAGEMENT("38.03.02", "Менеджмент", "ИЭМ");


    private final String codeSpecialty;
    private final String titleSpecialty;
    private final String institute;

    YstuSpecialtyCode(String codeSpecialty, String titleSpecialty, String institute) {
        this.codeSpecialty = codeSpecialty;
        this.titleSpecialty = titleSpecialty;
        this.institute = institute;
    }

    public String getCodeSpecialty() { return codeSpecialty; }
    public String getTitleSpecialty() { return titleSpecialty; }
    public String getInstitute() { return institute; }

    // Для поиска по коду
    public static YstuSpecialtyCode fromCodeSpecialty(String code) {
        for (YstuSpecialtyCode spec : values()) {
            if (spec.codeSpecialty.equals(code)) {
                return spec;
            }
        }
        throw new IllegalArgumentException("Неизвестный код специальности: " + code);
    }
}
