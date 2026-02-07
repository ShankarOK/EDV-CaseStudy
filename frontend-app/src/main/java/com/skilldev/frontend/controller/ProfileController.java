package com.skilldev.frontend.controller;

import com.skilldev.frontend.web.AuthInterceptor;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @GetMapping
    public String profile(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        model.addAttribute("role", session.getAttribute(AuthInterceptor.SESSION_ROLE));
        return "profile";
    }
}
