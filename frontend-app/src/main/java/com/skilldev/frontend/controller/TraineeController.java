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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/trainee")
public class TraineeController {

    private final GatewayApiService api;

    public TraineeController(GatewayApiService api) {
        this.api = api;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        try {
            model.addAttribute("courses", api.getList(session, "/courses/active", new ParameterizedTypeReference<>() {}));
            model.addAttribute("certificates", List.of()); // could get by traineeId if we had it in session
        } catch (Exception e) {
            model.addAttribute("courses", List.of());
            model.addAttribute("certificates", List.of());
        }
        return "trainee/dashboard";
    }

    @GetMapping("/courses")
    public String courses(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        try {
            model.addAttribute("courses", api.getList(session, "/courses/active", new ParameterizedTypeReference<>() {}));
        } catch (Exception e) {
            model.addAttribute("courses", List.of());
        }
        return "trainee/courses";
    }

    @GetMapping("/enroll")
    public String enrollForm(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        try {
            model.addAttribute("courses", api.getList(session, "/courses/active", new ParameterizedTypeReference<>() {}));
        } catch (Exception e) {
            model.addAttribute("courses", List.of());
        }
        return "trainee/enroll";
    }

    @PostMapping("/enroll")
    public String enroll(@RequestParam Long traineeId, @RequestParam Long courseId,
                        HttpSession session, RedirectAttributes ra) {
        try {
            api.post(session, "/trainees/" + traineeId + "/enroll?courseId=" + courseId, null, Map.class);
            ra.addFlashAttribute("message", "Enrolled successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Enrollment failed (e.g. already enrolled).");
        }
        return "redirect:/trainee/courses";
    }

    @GetMapping("/assessments")
    public String assessments(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        try {
            model.addAttribute("assessments", api.getList(session, "/assessments", new ParameterizedTypeReference<>() {}));
        } catch (Exception e) {
            model.addAttribute("assessments", List.of());
        }
        return "trainee/assessments";
    }

    @GetMapping("/assessments/{id}/take")
    public String takeAssessment(@PathVariable Long id, HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        model.addAttribute("assessmentId", id);
        try {
            List<Map<String, Object>> questions = api.getList(session, "/assessments/" + id + "/questions", new ParameterizedTypeReference<>() {});
            model.addAttribute("questions", questions != null ? questions : List.of());
        } catch (Exception e) {
            model.addAttribute("questions", List.of());
        }
        return "trainee/take-assessment";
    }

    @PostMapping("/assessments/{id}/submit")
    public String submitAssessment(@PathVariable Long id, @RequestParam Long traineeId,
                                   @RequestParam Map<String, String> allParams,
                                   HttpSession session, RedirectAttributes ra) {
        try {
            Map<String, String> answersMap = allParams.entrySet().stream()
                    .filter(e -> e.getKey().startsWith("q_"))
                    .collect(Collectors.toMap(e -> e.getKey().replace("q_", ""), Map.Entry::getValue));
            Map<String, Object> body = new java.util.HashMap<>();
            body.put("traineeId", traineeId);
            body.put("answers", answersMap);
            api.post(session, "/assessments/" + id + "/submit", body, Map.class);
            ra.addFlashAttribute("message", "Assessment submitted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Submit failed.");
        }
        return "redirect:/trainee/results";
    }

    @GetMapping("/results")
    public String results(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        try {
            // Use traineeId=1 for demo; in real app get from session or profile
            model.addAttribute("submissions", api.getList(session, "/assessments/trainee/1/submissions", new ParameterizedTypeReference<>() {}));
        } catch (Exception e) {
            model.addAttribute("submissions", List.of());
        }
        return "trainee/results";
    }

    @GetMapping("/certificates")
    public String certificates(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        try {
            model.addAttribute("certificates", api.getList(session, "/certificates/trainee/1", new ParameterizedTypeReference<>() {}));
        } catch (Exception e) {
            model.addAttribute("certificates", List.of());
        }
        return "trainee/certificates";
    }

    @GetMapping("/feedback")
    public String feedback(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        return "trainee/feedback";
    }
}
