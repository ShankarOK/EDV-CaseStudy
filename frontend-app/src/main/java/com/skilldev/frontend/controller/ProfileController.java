package com.skilldev.frontend.controller;

import com.skilldev.frontend.client.GatewayApiService;
import com.skilldev.frontend.web.AuthInterceptor;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatusCodeException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final GatewayApiService gatewayApi;

    public ProfileController(GatewayApiService gatewayApi) {
        this.gatewayApi = gatewayApi;
    }

    @GetMapping
    public String profile(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        model.addAttribute("role", session.getAttribute(AuthInterceptor.SESSION_ROLE));
        try {
            GatewayApiService.MeResponse me = gatewayApi.get(session, "/auth/me", GatewayApiService.MeResponse.class);
            if (me != null) {
                model.addAttribute("displayName", me.displayName() != null ? me.displayName() : "");
                model.addAttribute("email", me.email() != null ? me.email() : "");
            } else {
                model.addAttribute("displayName", "");
                model.addAttribute("email", "");
            }
        } catch (Exception e) {
            model.addAttribute("displayName", "");
            model.addAttribute("email", "");
        }
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(HttpSession session,
                                @RequestParam(defaultValue = "") String displayName,
                                @RequestParam(defaultValue = "") String email,
                                RedirectAttributes redirectAttrs) {
        try {
            gatewayApi.put(session, "/auth/profile", java.util.Map.of("displayName", displayName, "email", email), GatewayApiService.MeResponse.class);
            redirectAttrs.addFlashAttribute("message", "Profile updated successfully.");
        } catch (HttpStatusCodeException e) {
            String err = e.getResponseBodyAsString();
            if (err != null && !err.isBlank() && err.length() < 200) {
                redirectAttrs.addFlashAttribute("error", err);
            } else {
                redirectAttrs.addFlashAttribute("error", "Update failed: " + e.getStatusCode());
            }
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Update failed: " + e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/password")
    public String changePassword(HttpSession session,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 RedirectAttributes redirectAttrs) {
        if (newPassword == null || newPassword.isBlank()) {
            redirectAttrs.addFlashAttribute("error", "New password cannot be empty.");
            return "redirect:/profile";
        }
        try {
            gatewayApi.put(session, "/auth/password", java.util.Map.of("currentPassword", currentPassword, "newPassword", newPassword), Void.class);
            redirectAttrs.addFlashAttribute("message", "Password changed successfully.");
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().value() == 401) {
                redirectAttrs.addFlashAttribute("error", "Current password is incorrect.");
            } else {
                String err = e.getResponseBodyAsString();
                redirectAttrs.addFlashAttribute("error", err != null && !err.isBlank() && err.length() < 200 ? err : "Password change failed: " + e.getStatusCode());
            }
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Password change failed: " + e.getMessage());
        }
        return "redirect:/profile";
    }
}
