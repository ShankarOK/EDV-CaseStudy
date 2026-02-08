package com.skilldev.security.controller;

import com.skilldev.security.config.UserEntityMapping;
import com.skilldev.security.dto.*;
import com.skilldev.security.service.ProfileStore;
import com.skilldev.security.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserEntityMapping userEntityMapping;
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final ProfileStore profileStore;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                         UserEntityMapping userEntityMapping, UserDetailsManager userDetailsManager,
                         PasswordEncoder passwordEncoder, ProfileStore profileStore) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userEntityMapping = userEntityMapping;
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
        this.profileStore = profileStore;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst()
                .orElse("TRAINEE");
        String token = jwtUtil.generateToken(username, role);
        return ResponseEntity.ok(new LoginResponse(token, username, role));
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(false);
        }
        String token = authHeader.substring(7);
        return ResponseEntity.ok(jwtUtil.validateToken(token));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String username = usernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String role = jwtUtil.getRoleFromToken(authHeader.substring(7));
        Long entityId = userEntityMapping.getEntityId(username);
        ProfileRequest profile = profileStore.get(username);
        String displayName = profile != null ? profile.displayName() : null;
        String email = profile != null ? profile.email() : null;
        return ResponseEntity.ok(MeResponse.of(username, role, entityId, displayName, email));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                @RequestBody PasswordRequest request) {
        String username = usernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (request.currentPassword() == null || request.newPassword() == null || request.newPassword().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        UserDetails current = userDetailsManager.loadUserByUsername(username);
        if (!passwordEncoder.matches(request.currentPassword(), current.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String[] roles = current.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                .toArray(String[]::new);
        UserDetails updated = User.builder()
                .username(username)
                .password(passwordEncoder.encode(request.newPassword()))
                .roles(roles)
                .build();
        userDetailsManager.updateUser(updated);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/profile")
    public ResponseEntity<MeResponse> updateProfile(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                    @RequestBody ProfileRequest request) {
        String username = usernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (request != null) {
            profileStore.put(username, request);
        }
        String role = jwtUtil.getRoleFromToken(authHeader.substring(7));
        Long entityId = userEntityMapping.getEntityId(username);
        ProfileRequest profile = profileStore.get(username);
        String displayName = profile != null ? profile.displayName() : null;
        String email = profile != null ? profile.email() : null;
        return ResponseEntity.ok(MeResponse.of(username, role, entityId, displayName, email));
    }

    private String usernameFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return null;
        }
        return jwtUtil.getUsernameFromToken(token);
    }
}
