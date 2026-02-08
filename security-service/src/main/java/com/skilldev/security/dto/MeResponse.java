package com.skilldev.security.dto;

public record MeResponse(String username, String role, Long entityId, String displayName, String email) {

    public static MeResponse of(String username, String role, Long entityId, String displayName, String email) {
        return new MeResponse(username, role, entityId, displayName, email);
    }
}
