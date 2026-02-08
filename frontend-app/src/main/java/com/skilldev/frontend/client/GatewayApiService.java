package com.skilldev.frontend.client;

import com.skilldev.frontend.web.AuthInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Service
public class GatewayApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiPrefix;

    public GatewayApiService(@Value("${app.api-prefix}") String apiPrefix) {
        this.apiPrefix = apiPrefix;
    }

    public String getApiPrefix() {
        return apiPrefix;
    }

    private HttpHeaders headersWithAuth(HttpSession session) {
        String token = (String) session.getAttribute(AuthInterceptor.SESSION_TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null && !token.isBlank()) {
            headers.setBearerAuth(token);
        }
        return headers;
    }

    public LoginResponse login(String username, String password) {
        String url = apiPrefix + "/auth/login";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = Map.of("username", username, "password", password);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<LoginResponse> res = restTemplate.postForEntity(url, entity, LoginResponse.class);
        return res.getBody();
    }

    public <T> T get(HttpSession session, String path, Class<T> responseType) {
        String url = apiPrefix + path;
        HttpEntity<Void> entity = new HttpEntity<>(headersWithAuth(session));
        ResponseEntity<T> res = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
        return res.getBody();
    }

    /** GET and return full response (e.g. for PDF download to forward headers). */
    public ResponseEntity<byte[]> getForDownload(HttpSession session, String path) {
        String url = apiPrefix + path;
        HttpEntity<Void> entity = new HttpEntity<>(headersWithAuth(session));
        return restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
    }

    public <T> List<T> getList(HttpSession session, String path, ParameterizedTypeReference<List<T>> typeRef) {
        String url = apiPrefix + path;
        HttpEntity<Void> entity = new HttpEntity<>(headersWithAuth(session));
        ResponseEntity<List<T>> res = restTemplate.exchange(url, HttpMethod.GET, entity, typeRef);
        return res.getBody() != null ? res.getBody() : List.of();
    }

    public <T> T post(HttpSession session, String path, Object body, Class<T> responseType) {
        String url = apiPrefix + path;
        HttpEntity<Object> entity = new HttpEntity<>(body, headersWithAuth(session));
        ResponseEntity<T> res = restTemplate.postForEntity(url, entity, responseType);
        return res.getBody();
    }

    public <T> T postNoBody(HttpSession session, String path, Class<T> responseType) {
        String url = apiPrefix + path;
        HttpEntity<Void> entity = new HttpEntity<>(headersWithAuth(session));
        ResponseEntity<T> res = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
        return res.getBody();
    }

    public <T> T put(HttpSession session, String path, Object body, Class<T> responseType) {
        String url = apiPrefix + path;
        HttpEntity<Object> entity = new HttpEntity<>(body, headersWithAuth(session));
        ResponseEntity<T> res = restTemplate.exchange(url, HttpMethod.PUT, entity, responseType);
        return res.getBody();
    }

    public void delete(HttpSession session, String path) {
        String url = apiPrefix + path;
        HttpEntity<Void> entity = new HttpEntity<>(headersWithAuth(session));
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }

    public boolean healthCheck() {
        try {
            restTemplate.getForEntity(apiPrefix + "/auth/login", String.class);
            return true;
        } catch (RestClientException e) {
            return false;
        }
    }

    public record LoginResponse(String token, String username, String role) {}

    /** Phase 2: response from GET /auth/me */
    public record MeResponse(String username, String role, Long entityId, String displayName, String email) {}
}
