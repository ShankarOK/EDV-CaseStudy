package com.skilldev.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserEntityMapping {

    private final Map<String, Long> usernameToEntityId = new HashMap<>();

    public UserEntityMapping(
            @Value("${app.user-entities.trainee:1}") long traineeEntityId,
            @Value("${app.user-entities.trainer:1}") long trainerEntityId,
            @Value("${app.user-entities.admin:0}") long adminEntityId) {
        usernameToEntityId.put("trainee", traineeEntityId);
        usernameToEntityId.put("trainer", trainerEntityId);
        usernameToEntityId.put("admin", adminEntityId);
    }

    public Long getEntityId(String username) {
        return usernameToEntityId.getOrDefault(username != null ? username.toLowerCase() : "", 0L);
    }
}
