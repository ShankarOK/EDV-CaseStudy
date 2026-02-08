package com.skilldev.frontend.controller;

import com.skilldev.frontend.client.GatewayApiService;
import com.skilldev.frontend.web.AuthInterceptor;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final GatewayApiService gatewayApi;

    public AuthController(GatewayApiService gatewayApi) {
        this.gatewayApi = gatewayApi;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String redirect, Model model) {
        model.addAttribute("redirect", redirect != null ? redirect : "");
        return "login";
    }

    @PostMapping("/auth/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String redirect,
            HttpSession session,
            RedirectAttributes ra) {
        try {
            GatewayApiService.LoginResponse res = gatewayApi.login(username, password);
            session.setAttribute(AuthInterceptor.SESSION_TOKEN, res.token());
            session.setAttribute(AuthInterceptor.SESSION_USERNAME, res.username());
            session.setAttribute(AuthInterceptor.SESSION_ROLE, res.role());
            try {
                GatewayApiService.MeResponse me = gatewayApi.get(session, "/auth/me", GatewayApiService.MeResponse.class);
                if (me != null && me.entityId() != null) {
                    if ("TRAINEE".equalsIgnoreCase(res.role())) {
                        session.setAttribute(AuthInterceptor.SESSION_TRAINEE_ID, me.entityId());
                    } else if ("TRAINER".equalsIgnoreCase(res.role())) {
                        session.setAttribute(AuthInterceptor.SESSION_TRAINER_ID, me.entityId());
                    }
                }
            } catch (Exception ignored) {
            }
            if (redirect != null && !redirect.isBlank() && !redirect.equals("/login")) {
                return "redirect:" + redirect;
            }
            return switch (res.role().toUpperCase()) {
                case "ADMIN" -> "redirect:/admin/dashboard";
                case "TRAINER" -> "redirect:/trainer/dashboard";
                default -> "redirect:/trainee/dashboard";
            };
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Invalid username or password.");
            return "redirect:/login";
        }
    }

    @PostMapping("/auth/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
