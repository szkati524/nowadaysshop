package com.example.order_service.infrastructure.adapters.in.web.dto;

import com.example.order_service.application.ports.in.OrderUseCase;
import com.example.order_service.domain.model.Order;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

public class OrderController {

    private final OrderUseCase orderUseCase;


    public OrderController(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequest request){
        Order order = orderUseCase.createOrder(request.userId(),request.productId(),request.quantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID id){
        return ResponseEntity.ok(orderUseCase.getOrderById(id));
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable UUID userId){
        return ResponseEntity.ok(orderUseCase.getOrdersByUserId(userId));
    }
}
