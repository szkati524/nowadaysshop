package com.example.order_service.application.ports.out.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(UUID id, String name, BigDecimal price,Integer stockQuantity) {
}
