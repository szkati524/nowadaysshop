package com.example.order_service.infrastructure.adapters.out.persistence;

import com.example.order_service.application.ports.out.OrderRepositoryPort;
import com.example.order_service.domain.model.Order;
import com.example.order_service.domain.model.OrderStatus;
import com.example.order_service.infrastructure.adapters.out.persistence.entity.OrderEntity;
import com.example.order_service.infrastructure.adapters.out.persistence.repository.SpringDataOrderRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class OrderPersistenceAdapter implements OrderRepositoryPort {
    private final SpringDataOrderRepository repository;

    public OrderPersistenceAdapter(SpringDataOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = mapToEntity(order);
        OrderEntity saved = repository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    @Override
    public List<Order> findByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(this::mapToDomain)
                .toList();
    }
    private OrderEntity mapToEntity(Order order){
        return new OrderEntity(
                order.getId(),
                order.getUserId(),
                order.getProductId(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getStatus().name(),
                order.getCreatedAt()
        );
    }
    private Order mapToDomain(OrderEntity entity){
        return new Order(
                entity.getId(),
                entity.getUserId(),
                entity.getProductId(),
                entity.getQuantity(),
                entity.getTotalPrice(),
                OrderStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt()
        );
    }
}
