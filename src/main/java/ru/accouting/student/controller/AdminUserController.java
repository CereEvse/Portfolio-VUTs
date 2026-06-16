package ru.accouting.student.controller;

import ru.accouting.student.model.User;
import ru.accouting.student.model.UserAuthority;
import ru.accouting.student.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = adminUserService.getAllUsers();
        model.addAttribute("users", users);
        return "admin-users";
    }

    @GetMapping("/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = adminUserService.getUser(id);
        model.addAttribute("user", user);
        model.addAttribute("allAuthorities", EnumSet.allOf(UserAuthority.class));
        return "admin-user-edit";
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id,
                             @RequestParam String login,
                             @RequestParam(required = false) String password,
                             @RequestParam(name = "authority") String authorityRaw) {

        UserAuthority authority = UserAuthority.valueOf(authorityRaw);

        adminUserService.updateUser(id, login, password, authority);

        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/new")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allAuthorities", EnumSet.allOf(UserAuthority.class));
        return "admin-user-create";
    }

    @PostMapping("/new")
    public String createUser(@RequestParam String login,
                             @RequestParam(required = false) String password,
                             @RequestParam(name = "authority") String authorityRaw) {

        UserAuthority authority = UserAuthority.valueOf(authorityRaw);
        adminUserService.createUser(login, password, authority);

        return "redirect:/admin/users";
    }
}
