package com.example.order_service.infrastructure.adapters.out.rest;

import com.example.order_service.application.ports.out.UserClientPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class UserRestClientAdapter implements UserClientPort {
    private final RestClient restClient;

    public UserRestClientAdapter(RestClient.Builder builder, @Value("${services.user.url:http://USER-SERVICE}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public void chargeWallet(UUID userId, BigDecimal amount) {
restClient.post()
        .uri("/api/users/{id}/wallet/charge", userId)
        .body(Map.of("amount",amount))
        .retrieve()
        .toBodilessEntity();
    }
}
