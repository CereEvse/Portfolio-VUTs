package ru.accouting.student.dto;

import ru.accouting.student.model.UserAuthority;

public record AdminUserUpdateRequest(
        String login,
        String password,
        UserAuthority authority
) {}
