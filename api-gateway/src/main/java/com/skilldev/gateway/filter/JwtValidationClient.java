package com.skilldev.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class JwtValidationClient {

    private final WebClient webClient;
    private final String validateUri;

    public JwtValidationClient(
            WebClient.Builder webClientBuilder,
            @Value("${app.security-service-url:http://localhost:8081}") String securityServiceUrl) {
        this.validateUri = securityServiceUrl + "/auth/validate";
        this.webClient = webClientBuilder.build();
    }

    public Mono<Boolean> validate(String authorizationHeader) {
        return webClient.post()
                .uri(validateUri)
                .header("Authorization", authorizationHeader)
                .retrieve()
                .bodyToMono(Boolean.class);
    }
}
