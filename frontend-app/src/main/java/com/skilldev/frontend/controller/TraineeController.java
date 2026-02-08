package com.skilldev.frontend.controller;

import com.skilldev.frontend.client.GatewayApiService;
import com.skilldev.frontend.web.AuthInterceptor;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        Long traineeId = (Long) session.getAttribute(AuthInterceptor.SESSION_TRAINEE_ID);
        if (traineeId == null) {
            return "redirect:/login?redirect=/trainee/dashboard";
        }
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        try {
            model.addAttribute("courses", api.getList(session, "/courses/active", new ParameterizedTypeReference<>() {}));
            model.addAttribute("certificates", api.getList(session, "/certificates/trainee/" + traineeId, new ParameterizedTypeReference<>() {}));
        } catch (Exception e) {
            model.addAttribute("courses", List.of());
            model.addAttribute("certificates", List.of());
        }
        return "trainee/dashboard";
    }

    @GetMapping("/courses")
    public String courses(HttpSession session, Model model) {
        if (session.getAttribute(AuthInterceptor.SESSION_TRAINEE_ID) == null) {
            return "redirect:/login?redirect=/trainee/courses";
        }
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
        if (session.getAttribute(AuthInterceptor.SESSION_TRAINEE_ID) == null) {
            return "redirect:/login?redirect=/trainee/enroll";
        }
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        try {
            model.addAttribute("courses", api.getList(session, "/courses/active", new ParameterizedTypeReference<>() {}));
        } catch (Exception e) {
            model.addAttribute("courses", List.of());
        }
        return "trainee/enroll";
    }

    @PostMapping("/enroll")
    public String enroll(@RequestParam Long courseId, HttpSession session, RedirectAttributes ra) {
        Long traineeId = (Long) session.getAttribute(AuthInterceptor.SESSION_TRAINEE_ID);
        if (traineeId == null) {
            ra.addFlashAttribute("error", "Session expired. Please log in again.");
            return "redirect:/login";
        }
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
        if (session.getAttribute(AuthInterceptor.SESSION_TRAINEE_ID) == null) {
            return "redirect:/login?redirect=/trainee/assessments";
        }
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
        if (session.getAttribute(AuthInterceptor.SESSION_TRAINEE_ID) == null) {
            return "redirect:/login?redirect=/trainee/assessments/" + id + "/take";
        }
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
    public String submitAssessment(@PathVariable Long id, @RequestParam Map<String, String> allParams,
                                   HttpSession session, RedirectAttributes ra) {
        Long traineeId = (Long) session.getAttribute(AuthInterceptor.SESSION_TRAINEE_ID);
        if (traineeId == null) {
            ra.addFlashAttribute("error", "Session expired. Please log in again.");
            return "redirect:/login";
        }
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
        Long traineeId = (Long) session.getAttribute(AuthInterceptor.SESSION_TRAINEE_ID);
        if (traineeId == null) {
            return "redirect:/login?redirect=/trainee/results";
        }
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        try {
            model.addAttribute("submissions", api.getList(session, "/assessments/trainee/" + traineeId + "/submissions", new ParameterizedTypeReference<>() {}));
        } catch (Exception e) {
            model.addAttribute("submissions", List.of());
        }
        return "trainee/results";
    }

    @GetMapping("/certificates")
    public String certificates(HttpSession session, Model model) {
        Long traineeId = (Long) session.getAttribute(AuthInterceptor.SESSION_TRAINEE_ID);
        if (traineeId == null) {
            return "redirect:/login?redirect=/trainee/certificates";
        }
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        try {
            model.addAttribute("certificates", api.getList(session, "/certificates/trainee/" + traineeId, new ParameterizedTypeReference<>() {}));
        } catch (Exception e) {
            model.addAttribute("certificates", List.of());
        }
        return "trainee/certificates";
    }

    @GetMapping("/certificates/{id}/download")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable Long id, HttpSession session) {
        Long traineeId = (Long) session.getAttribute(AuthInterceptor.SESSION_TRAINEE_ID);
        if (traineeId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ResponseEntity<byte[]> response = api.getForDownload(session, "/certificates/" + id + "/download");
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
            if (response.getHeaders().getContentDisposition() != null) {
                headers.setContentDisposition(response.getHeaders().getContentDisposition());
            } else {
                headers.setContentDispositionFormData("attachment", "certificate-" + id + ".pdf");
            }
            return ResponseEntity.ok().headers(headers).body(response.getBody());
        }
        return ResponseEntity.status(response.getStatusCode()).build();
    }

    @GetMapping("/feedback")
    public String feedback(HttpSession session, Model model) {
        Long traineeId = (Long) session.getAttribute(AuthInterceptor.SESSION_TRAINEE_ID);
        if (traineeId == null) {
            return "redirect:/login?redirect=/trainee/feedback";
        }
        model.addAttribute("username", session.getAttribute(AuthInterceptor.SESSION_USERNAME));
        try {
            model.addAttribute("feedbackList", api.getList(session, "/feedback/trainee/" + traineeId, new ParameterizedTypeReference<>() {}));
        } catch (Exception e) {
            model.addAttribute("feedbackList", List.of());
        }
        return "trainee/feedback";
    }

    @PostMapping("/feedback")
    public String submitFeedback(@RequestParam Integer rating, @RequestParam String comment,
                                 @RequestParam(required = false) Long trainerId, @RequestParam(required = false) Long courseId,
                                 HttpSession session, RedirectAttributes ra) {
        Long traineeId = (Long) session.getAttribute(AuthInterceptor.SESSION_TRAINEE_ID);
        if (traineeId == null) {
            ra.addFlashAttribute("error", "Session expired. Please log in again.");
            return "redirect:/login";
        }
        try {
            Map<String, Object> body = new java.util.HashMap<>();
            body.put("traineeId", traineeId);
            body.put("rating", rating);
            body.put("comment", comment != null ? comment : "");
            if (trainerId != null) body.put("trainerId", trainerId);
            if (courseId != null) body.put("courseId", courseId);
            api.post(session, "/feedback", body, Map.class);
            ra.addFlashAttribute("message", "Feedback submitted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to submit (rating 1–5, comment required).");
        }
        return "redirect:/trainee/feedback";
    }
}
