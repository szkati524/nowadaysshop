package com.example.order_service.application.service;

import com.example.order_service.application.ports.in.OrderUseCase;

import com.example.order_service.application.ports.out.CatalogClientPort;
import com.example.order_service.application.ports.out.OrderRepositoryPort;
import com.example.order_service.application.ports.out.UserClientPort;
import com.example.order_service.application.ports.out.dto.ProductResponse;
import com.example.order_service.domain.event.OrderCreatedEvent;
import com.example.order_service.domain.exception.OrderNotFoundException;
import com.example.order_service.domain.model.Order;
import com.example.order_service.domain.service.OrderDomainService;
import com.example.order_service.infrastructure.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


import java.util.List;
import java.util.UUID;

public class OrderApplicationService implements OrderUseCase {
    private final OrderRepositoryPort orderRepositoryPort;
    private final UserClientPort userClientPort;
    private final CatalogClientPort catalogClientPort;
    private final OrderDomainService orderDomainService;

    private final RabbitTemplate rabbitTemplate;

    public OrderApplicationService(OrderRepositoryPort orderRepositoryPort, UserClientPort userClientPort, CatalogClientPort catalogClientPort, OrderDomainService orderDomainService, RabbitTemplate rabbitTemplate) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.userClientPort = userClientPort;
        this.catalogClientPort = catalogClientPort;
        this.orderDomainService = orderDomainService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Order createOrder(UUID userId, UUID productId, Integer quantity) {
        ProductResponse product = catalogClientPort.getProduct(productId);
        Order order = orderDomainService.createPendingOrder(userId, productId, quantity, product.price());


        Order savedPendingOrder = orderRepositoryPort.save(order);

        try {
            catalogClientPort.reserveStock(productId, quantity);
            userClientPort.chargeWallet(userId, savedPendingOrder.getTotalPrice());

            orderDomainService.completeOrder(savedPendingOrder);
            Order completedOrder = orderRepositoryPort.save(savedPendingOrder);


            OrderCreatedEvent event = new OrderCreatedEvent(
                    completedOrder.getId(),
                    completedOrder.getUserId(),
                    completedOrder.getProductId(),
                    completedOrder.getQuantity(),
                    completedOrder.getTotalPrice()
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    "order.created",
                    event
            );

            return completedOrder;

        } catch (Exception e) {
            orderDomainService.cancelOrder(savedPendingOrder);
            orderRepositoryPort.save(savedPendingOrder);
            throw new RuntimeException("Błąd podczas przetwarzania zamówienia: " + e.getMessage(), e);
        }
    }


    @Override
    public Order getOrderById(UUID id) {
        return orderRepositoryPort.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Nie znaleziono zamówienia o ID: " + id));
    }

    @Override
    public List<Order> getOrdersByUserId(UUID userId) {
        return orderRepositoryPort.findByUserId(userId);
    }
}
