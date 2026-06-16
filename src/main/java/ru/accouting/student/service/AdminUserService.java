package ru.accouting.student.service;

import ru.accouting.student.model.User;
import ru.accouting.student.model.UserAuthority;
import ru.accouting.student.model.UserRole;
import ru.accouting.student.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + id));
    }

//    @Transactional
//    public void updateUser(Long id,
//                           String login,
//                           String rawPassword,
//                           UserAuthority authority) {
//
//        User user = getUser(id);
//
//        user.setLogin(login);
//        if (rawPassword != null && !rawPassword.isBlank()) {
//            user.setPassword(passwordEncoder.encode(rawPassword));
//        }
//
//        if (user.getUserRoles() == null) {
//            user.setUserRoles(new ArrayList<>());
//        }
//
//        user.getUserRoles().clear();
//
//        if (authority != null) {
//            UserRole role = new UserRole();
//            role.setUserAuthority(authority);
//            role.setUser(user);
//            user.getUserRoles().add(role);
//        }
//
//        userRepository.save(user);
//    }

    @Transactional
    public void updateUser(Long id,
                           String login,
                           String rawPassword,
                           UserAuthority authority) {

        User user = getUser(id);

        user.setLogin(login);

        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }

        user.getUserRoles().clear();

        if (authority != null) {
            UserRole role = new UserRole();
            role.setUserAuthority(authority);
            role.setUser(user);
            user.getUserRoles().add(role);
        }
    }

    @Transactional
    public void createUser(String login, String rawPassword, UserAuthority authority) {
        User user = new User();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(rawPassword));

        UserRole role = new UserRole();
        role.setUserAuthority(authority);
        role.setUser(user);

        user.setUserRoles(new ArrayList<>(List.of(role)));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUser(id);
        userRepository.delete(user);
    }


}


