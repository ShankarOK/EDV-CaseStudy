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
@RequestMapping("/trainer")
public class TrainerController {

    private final GatewayApiService api;

    public TrainerController(GatewayApiService api) {
        this.api = api;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        try {
            List<Map<String, Object>> assessments = api.getList(session, "/assessments", new ParameterizedTypeReference<>() {});
            model.addAttribute("assessments", assessments != null ? assessments : List.of());
        } catch (Exception e) {
            model.addAttribute("assessments", List.of());
        }
        return "trainer/dashboard";
    }

    @GetMapping("/assessments")
    public String assessments(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        try {
            model.addAttribute("assessments", api.getList(session, "/assessments", new ParameterizedTypeReference<>() {}));
        } catch (Exception e) {
            model.addAttribute("assessments", List.of());
        }
        return "trainer/assessments";
    }

    @GetMapping("/assessments/create")
    public String createAssessmentForm(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        try {
            model.addAttribute("courses", api.getList(session, "/courses/active", new ParameterizedTypeReference<>() {}));
        } catch (Exception e) {
            model.addAttribute("courses", List.of());
        }
        return "trainer/assessments-create";
    }

    @PostMapping("/assessments/create")
    public String createAssessment(@RequestParam String title, @RequestParam Long courseId,
                                  @RequestParam Integer passingScore, @RequestParam Integer maxScore,
                                  @RequestParam String dueDate, HttpSession session, RedirectAttributes ra) {
        try {
            Map<String, Object> body = Map.of(
                    "title", title,
                    "courseId", courseId,
                    "passingScore", passingScore,
                    "maxScore", maxScore,
                    "dueDate", dueDate,
                    "createdByTrainerId", 1L,
                    "status", "PUBLISHED"
            );
            api.post(session, "/assessments", body, Map.class);
            ra.addFlashAttribute("message", "Assessment created.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to create assessment.");
        }
        return "redirect:/trainer/assessments";
    }

    @GetMapping("/assessments/{id}/submissions")
    public String submissions(@PathVariable Long id, HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        model.addAttribute("assessmentId", id);
        try {
            model.addAttribute("submissions", api.getList(session, "/assessments/" + id + "/submissions", new ParameterizedTypeReference<>() {}));
        } catch (Exception e) {
            model.addAttribute("submissions", List.of());
        }
        return "trainer/submissions";
    }

    @PostMapping("/assessments/submissions/{submissionId}/evaluate")
    public String evaluate(@PathVariable Long submissionId, @RequestParam(required = false) Integer score,
                          @RequestParam Long trainerId, @RequestParam(required = false) String courseName,
                          @RequestParam(required = false) Long assessmentId,
                          HttpSession session, RedirectAttributes ra) {
        try {
            StringBuilder path = new StringBuilder("/assessments/submissions/").append(submissionId).append("/evaluate?trainerId=").append(trainerId);
            if (courseName != null && !courseName.isBlank()) path.append("&courseName=").append(java.net.URLEncoder.encode(courseName, java.nio.charset.StandardCharsets.UTF_8));
            if (score != null) path.append("&score=").append(score);
            api.postNoBody(session, path.toString(), Map.class);
            ra.addFlashAttribute("message", "Submission evaluated. Certificate issued if passed.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to evaluate.");
        }
        if (assessmentId != null) {
            return "redirect:/trainer/assessments/" + assessmentId + "/submissions";
        }
        return "redirect:/trainer/assessments";
    }

    @GetMapping("/results")
    public String results(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        return "trainer/results";
    }

    @GetMapping("/feedback")
    public String feedback(HttpSession session, Model model) {
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        return "trainer/feedback";
    }
}
