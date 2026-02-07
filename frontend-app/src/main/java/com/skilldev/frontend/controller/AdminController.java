package com.skilldev.frontend.controller;

import com.skilldev.frontend.client.GatewayApiService;
import com.skilldev.frontend.web.AuthInterceptor;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final GatewayApiService api;

    public AdminController(GatewayApiService api) {
        this.api = api;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        try {
            List<?> courses = api.getList(session, "/courses", new ParameterizedTypeReference<List<Map<String, Object>>>() {});
            List<?> trainees = api.getList(session, "/trainees", new ParameterizedTypeReference<List<Map<String, Object>>>() {});
            List<?> trainers = api.getList(session, "/trainers", new ParameterizedTypeReference<List<Map<String, Object>>>() {});
            model.addAttribute("courseCount", courses != null ? courses.size() : 0);
            model.addAttribute("traineeCount", trainees != null ? trainees.size() : 0);
            model.addAttribute("trainerCount", trainers != null ? trainers.size() : 0);
        } catch (Exception e) {
            model.addAttribute("courseCount", 0);
            model.addAttribute("traineeCount", 0);
            model.addAttribute("trainerCount", 0);
        }
        return "admin/dashboard";
    }

    @GetMapping("/courses")
    public String courses(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        try {
            List<Map<String, Object>> list = api.getList(session, "/courses", new ParameterizedTypeReference<>() {});
            model.addAttribute("courses", list != null ? list : List.of());
        } catch (Exception e) {
            model.addAttribute("courses", List.<Map<String, Object>>of());
        }
        return "admin/courses";
    }

    @GetMapping("/courses/register")
    public String registerCourseForm(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        try {
            List<Map<String, Object>> trainers = api.getList(session, "/trainers", new ParameterizedTypeReference<>() {});
            model.addAttribute("trainers", trainers != null ? trainers : List.of());
        } catch (Exception e) {
            model.addAttribute("trainers", List.of());
        }
        return "admin/courses-register";
    }

    @PostMapping("/courses/register")
    public String registerCourse(@RequestParam String title, @RequestParam String category,
                                 @RequestParam Integer durationHours, @RequestParam(required = false) String description,
                                 @RequestParam String startDate, @RequestParam String endDate,
                                 @RequestParam Long trainerId, HttpSession session, RedirectAttributes ra) {
        try {
            Map<String, Object> body = Map.of(
                    "title", title,
                    "category", category != null ? category : "",
                    "durationHours", durationHours,
                    "description", description != null ? description : "",
                    "startDate", startDate,
                    "endDate", endDate,
                    "trainerId", trainerId
            );
            api.post(session, "/courses", body, Map.class);
            ra.addFlashAttribute("message", "Course registered successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to register course. Check validation (e.g. duration, dates, trainer).");
        }
        return "redirect:/admin/courses";
    }

    @GetMapping("/assign-trainer")
    public String assignTrainer(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        try {
            model.addAttribute("courses", api.getList(session, "/courses", new ParameterizedTypeReference<>() {}));
            model.addAttribute("trainers", api.getList(session, "/trainers", new ParameterizedTypeReference<>() {}));
        } catch (Exception e) {
            model.addAttribute("courses", List.of());
            model.addAttribute("trainers", List.of());
        }
        return "admin/assign-trainer";
    }

    @PostMapping("/courses/{id}/trainer")
    public String assignTrainerToCourse(@PathVariable Long id, @RequestParam Long trainerId,
                                       HttpSession session, RedirectAttributes ra) {
        try {
            api.put(session, "/courses/" + id, Map.of("trainerId", trainerId), Map.class);
            ra.addFlashAttribute("message", "Trainer assigned.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to assign trainer.");
        }
        return "redirect:/admin/assign-trainer";
    }
}
