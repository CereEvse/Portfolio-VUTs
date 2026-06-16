package ru.accouting.student.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.accouting.student.model.User;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SecurityUtils {

    /**
     * Возвращает ID текущего авторизованного пользователя.
     * Объект principal должен быть экземпляром ru.accouting.student.model.User.
     */
    public Optional<Long> getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            return Optional.ofNullable(user.getIdUser());
        }
        return Optional.empty();
    }

    /**
     * Проверяет, обладает ли текущий пользователь правами администратора или технолога.
     */
    public boolean isAdmin() {
        return hasAnyRole("FULL", "TECHNOLOGIST");
    }

    private boolean hasAnyRole(String... roles) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        var authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        for (String role : roles) {
            if (authorities.contains(role)) return true;
        }
        return false;
    }
}
