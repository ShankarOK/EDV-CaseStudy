package com.skilldev.security.dto;

public record LoginResponse(String token, String username, String role) {}
