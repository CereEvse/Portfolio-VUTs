package ru.accouting.student.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public String adminPage() {
        return "admin"; // templates/admin.html
    }

    @GetMapping("/admin-users")
    public String adminUsersPage() {
        return "admin-users"; // пока можно заглушкой
    }

//    @GetMapping("/admin-physical")
//    public String adminPhysicalPage() {
//        return "admin-physical"; // пока тоже заглушкой
//    }
}