package com.example.order_service.application.ports.out;

import com.example.order_service.application.ports.out.dto.ProductResponse;

import java.util.UUID;

public interface CatalogClientPort {
    ProductResponse getProduct(UUID productId);
    void reserveStock(UUID productId,int quantity);
}
