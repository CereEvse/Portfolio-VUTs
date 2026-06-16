package ru.accouting.student.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserAuthority implements GrantedAuthority {

    USER, //Просто пользователь
    TECHNOLOGIST, //Сотрудник с правами управления данными
    FULL, //Админ, который может менять права доступа
    STUDENT; //Студент для личного кабинета

    @Override
    public String getAuthority() {
        return this.name();
    }
}

