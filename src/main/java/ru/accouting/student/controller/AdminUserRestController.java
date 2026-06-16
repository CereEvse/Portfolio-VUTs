package ru.accouting.student.controller;

import ru.accouting.student.dto.AdminUserUpdateRequest;
import ru.accouting.student.model.User;
import ru.accouting.student.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
@PreAuthorize("hasAuthority('FULL')")
public class AdminUserRestController {

    private final AdminUserService adminUserService;

    @GetMapping
    public List<User> getAllUsers() {
        return adminUserService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return adminUserService.getUser(id);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id,
                           @RequestBody AdminUserUpdateRequest request) {

        adminUserService.updateUser(
                id,
                request.login(),
                request.password(),
                request.authority()
        );

        return adminUserService.getUser(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
    }
}
