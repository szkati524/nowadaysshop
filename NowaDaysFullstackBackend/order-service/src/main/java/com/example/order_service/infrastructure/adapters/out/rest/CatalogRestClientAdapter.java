package com.example.order_service.infrastructure.adapters.out.rest;

import com.example.order_service.application.ports.out.CatalogClientPort;
import com.example.order_service.application.ports.out.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;
@Component
public class CatalogRestClientAdapter implements CatalogClientPort {
    private final RestClient restClient;

    public CatalogRestClientAdapter(
            RestClient.Builder builder,
            @Value("${services.catalog.url:http://CATALOG-SERVICE}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public ProductResponse getProduct(UUID productId) {
        return restClient.get()
                .uri("/api/products/{id}", productId)
                .retrieve()
                .body(ProductResponse.class);
    }

    @Override
    public void reserveStock(UUID productId, int quantity) {
        restClient.post()
                .uri("/api/products/{id}/reserve", productId)
                .body(Map.of("quantity", quantity))
                .retrieve()
                .toBodilessEntity();
    }
}