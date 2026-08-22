package com.example.order_service.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Order {
    private final UUID id;
    private final UUID userId;
    private final UUID productId;
    private final Integer quantity;
    private final BigDecimal totalPrice;
    private OrderStatus status;
    private final Instant createdAt;

    public Order(UUID id, UUID userId, UUID productId, Integer quantity, BigDecimal totalPrice,OrderStatus status, Instant createdAt) {
        if (quantity != null && quantity <= 0){
            throw new IllegalArgumentException("Ilość produktów musi byc większa od zera");
        }
        if (totalPrice != null && totalPrice.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Łączna cena nie może być ujemna");
        }
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status != null ? status : OrderStatus.PENDING;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }
    public void markAsCompleted(){
        if (this.status == OrderStatus.CANCELLED){
            throw new IllegalStateException("Nie można sfinalizować amówienia ");
        }
        this.status = OrderStatus.COMPLETED;
    }
    public void markAsCancelled() {
        if (this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Nie można anulować już sfinalizowanego zamówienia");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
