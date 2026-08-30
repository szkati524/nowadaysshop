package com.example.order_service.application.ports.in;

import com.example.order_service.domain.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderUseCase {
    Order createOrder(UUID userId,UUID productId,Integer quantity);
    Order getOrderById(UUID id);
    List<Order> getOrdersByUserId(UUID userId);
}
