package ru.accouting.student.model;

public enum MilitaryAccountingSpecialty {
    SERGEANT("Сержант запаса «Командир отделения комплекса средств автоматизации командного пункта»"),
    RADAR("Солдат запаса «Оператор радиолокационной станции (комплекса)»"),
    UAV("Солдат запаса \"Оператор БПЛА\"");

    private final String title;

    MilitaryAccountingSpecialty(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
