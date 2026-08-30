package com.example.order_service.application.model;
import com.example.order_service.domain.event.OrderCreatedEvent;
import com.example.order_service.domain.model.Order;
import com.example.order_service.domain.model.OrderStatus;
import com.example.order_service.domain.service.OrderDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OrderDomainTest {
    private OrderDomainService orderDomainService;
    private UUID userId;
    private UUID productId;

    @BeforeEach
    void setUp() {
        orderDomainService = new OrderDomainService();
        userId = UUID.randomUUID();
        productId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("Testy tworzenia i kalkulacji zamówienia")
    class CreationAndCalculationTests {

        @Test

        void shouldCalculateTotalPriceCorrectlyTest() {

            Integer quantity = 3;
            BigDecimal unitPrice = new BigDecimal("150.00");
            BigDecimal expectedTotalPrice = new BigDecimal("450.00");


            Order order = orderDomainService.createPendingOrder(userId, productId, quantity, unitPrice);


            assertNotNull(order);
            assertEquals(expectedTotalPrice, order.getTotalPrice());
            assertEquals(OrderStatus.PENDING, order.getStatus());
        }

        @Test

        void shouldThrowExceptionWhenQuantityIsInvalidTest() {

            BigDecimal unitPrice = new BigDecimal("100.00");


            assertThrows(IllegalArgumentException.class, () ->
                    new Order(UUID.randomUUID(), userId, productId, 0, new BigDecimal("0.00"), OrderStatus.PENDING, null)
            );
        }

        @Test

        void shouldThrowExceptionWhenTotalPriceIsNegativeTest() {

            assertThrows(IllegalArgumentException.class, () ->
                    new Order(UUID.randomUUID(), userId, productId, 2, new BigDecimal("-50.00"), OrderStatus.PENDING, null)
            );
        }
    }

    @Nested
    @DisplayName("Testy zmiany statusów zamówienia")
    class StatusTransitionTests {

        @Test

        void shouldCompleteOrderSuccessfullyTest() {

            Order order = orderDomainService.createPendingOrder(userId, productId, 2, new BigDecimal("100.00"));


            orderDomainService.completeOrder(order);


            assertEquals(OrderStatus.COMPLETED, order.getStatus());
        }

        @Test

        void shouldCancelOrderSuccessfullyTest() {

            Order order = orderDomainService.createPendingOrder(userId, productId, 2, new BigDecimal("100.00"));


            orderDomainService.cancelOrder(order);


            assertEquals(OrderStatus.CANCELLED, order.getStatus());
        }

        @Test

        void shouldThrowExceptionWhenCompletingCancelledOrderTest() {

            Order order = orderDomainService.createPendingOrder(userId, productId, 2, new BigDecimal("100.00"));
            orderDomainService.cancelOrder(order);


            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                    orderDomainService.completeOrder(order)
            );
            assertEquals("Nie można sfinalizować zamówienia ", exception.getMessage());
        }

        @Test

        void shouldThrowExceptionWhenCancellingCompletedOrderTest() {

            Order order = orderDomainService.createPendingOrder(userId, productId, 2, new BigDecimal("100.00"));
            orderDomainService.completeOrder(order);


            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                    order.markAsCancelled()
            );
            assertEquals("Nie można anulować już sfinalizowanego zamówienia", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testy zdarzeń domenowych")
    class DomainEventTests {

        @Test

        void shouldCreateOrderCreatedEventCorrectlyTest() {

            UUID orderId = UUID.randomUUID();
            Integer quantity = 2;
            BigDecimal totalAmount = new BigDecimal("200.00");


            OrderCreatedEvent event = new OrderCreatedEvent(orderId, userId, productId, quantity, totalAmount);


            assertNotNull(event);
            assertEquals(orderId, event.orderId());
            assertEquals(userId, event.userId());
            assertEquals(productId, event.productId());
            assertEquals(quantity, event.quantity());
            assertEquals(totalAmount, event.totalAmount());
        }
    }
}

