package com.example.order_service.domain.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreatedEvent(UUID orderId, UUID userId,UUID productId,Integer quantity, BigDecimal totalAmount)  implements Serializable {
}
