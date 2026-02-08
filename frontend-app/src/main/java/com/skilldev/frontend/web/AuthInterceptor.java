package com.skilldev.frontend.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String SESSION_TOKEN = "token";
    public static final String SESSION_USERNAME = "username";
    public static final String SESSION_ROLE = "role";
    public static final String SESSION_TRAINEE_ID = "traineeId";
    public static final String SESSION_TRAINER_ID = "trainerId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        String token = session != null ? (String) session.getAttribute(SESSION_TOKEN) : null;
        if (token == null || token.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=" + request.getRequestURI());
            return false;
        }
        return true;
    }
}
