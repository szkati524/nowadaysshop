package com.example.order_service.infrastructure.adapters.out.rest;

import com.example.order_service.application.ports.out.UserClientPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class UserRestClientAdapter implements UserClientPort {
    private final RestClient restClient;

    public UserRestClientAdapter(
            RestClient.Builder builder,
            @Value("${services.user.url:http://USER-SERVICE}") String baseUrl) {

        this.restClient = builder
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                    if (authentication != null && authentication.getCredentials() != null) {
                        String token = authentication.getCredentials().toString();
                        request.getHeaders().setBearerAuth(token);
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

    public void chargeWallet(UUID userId, BigDecimal amount) {
        restClient.post()
                .uri("/api/users/{id}/withdraw", userId) // lub twój dedykowany endpoint
                .body(Map.of("amount", amount))
                .retrieve()
                .toBodilessEntity();
    }
}