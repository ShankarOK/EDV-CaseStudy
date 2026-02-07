package com.skilldev.frontend.controller;

import com.skilldev.frontend.web.AuthInterceptor;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session) {
        String role = (String) session.getAttribute(AuthInterceptor.SESSION_ROLE);
        if (role != null) {
            return switch (role.toUpperCase()) {
                case "ADMIN" -> "redirect:/admin/dashboard";
                case "TRAINER" -> "redirect:/trainer/dashboard";
                default -> "redirect:/trainee/dashboard";
            };
        }
        return "home";
    }
}
