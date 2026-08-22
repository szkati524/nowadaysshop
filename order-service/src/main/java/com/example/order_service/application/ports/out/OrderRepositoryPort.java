package com.example.order_service.application.ports.out;

import com.example.order_service.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(UUID id);
    List<Order> findByUserId(UUID userId);
}
