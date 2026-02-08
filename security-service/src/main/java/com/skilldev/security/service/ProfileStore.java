package com.skilldev.security.service;

import com.skilldev.security.dto.ProfileRequest;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProfileStore {

    private final Map<String, ProfileRequest> byUsername = new ConcurrentHashMap<>();

    public ProfileRequest get(String username) {
        return byUsername.get(username);
    }

    public void put(String username, ProfileRequest profile) {
        if (profile != null) {
            byUsername.put(username, profile);
        }
    }
}
