package com.example.order_service.domain.service;

import com.example.order_service.domain.model.Order;
import com.example.order_service.domain.model.OrderStatus;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class OrderDomainService {

    public Order createPendingOrder(UUID userId, UUID productId, Integer quantity, BigDecimal unitPrice){
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return new Order(
                null,
                userId,
                productId,
                quantity,
                totalPrice,
                OrderStatus.PENDING,
               null

        );
    }
    public void completeOrder(Order order){
        order.markAsCompleted();
    }
    public void cancelOrder(Order order){
        order.markAsCancelled();
    }
}
