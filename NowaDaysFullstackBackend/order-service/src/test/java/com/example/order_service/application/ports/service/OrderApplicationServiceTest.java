package com.example.order_service.application.ports.service;

import com.example.order_service.application.service.OrderApplicationService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.order_service.application.ports.out.CatalogClientPort;
import com.example.order_service.application.ports.out.OrderRepositoryPort;
import com.example.order_service.application.ports.out.UserClientPort;
import com.example.order_service.application.ports.out.dto.ProductResponse;
import com.example.order_service.domain.event.OrderCreatedEvent;
import com.example.order_service.domain.exception.OrderNotFoundException;
import com.example.order_service.domain.model.Order;
import com.example.order_service.domain.model.OrderStatus;
import com.example.order_service.domain.service.OrderDomainService;
import com.example.order_service.infrastructure.config.RabbitMQConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class OrderApplicationServiceTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @Mock
    private UserClientPort userClientPort;

    @Mock
    private CatalogClientPort catalogClientPort;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Spy
    private OrderDomainService orderDomainService;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @Captor
    private ArgumentCaptor<OrderCreatedEvent> eventCaptor;

    private OrderApplicationService orderApplicationService;

    private UUID userId;
    private UUID productId;
    private UUID orderId;
    private ProductResponse sampleProductResponse;

    @BeforeEach
    void setUp() {
        orderApplicationService = new OrderApplicationService(
                orderRepositoryPort,
                userClientPort,
                catalogClientPort,
                orderDomainService,
                rabbitTemplate
        );

        userId = UUID.randomUUID();
        productId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        sampleProductResponse = new ProductResponse(productId, "Słuchawki", new BigDecimal("100.00"),10);
    }

    @Nested
    @DisplayName("Testy metody createOrder")
    class CreateOrderTests {

        @Test

        void shouldCreateOrderSuccessfullyTest() {

            Integer quantity = 2;
            BigDecimal expectedTotal = new BigDecimal("200.00");

            when(catalogClientPort.getProduct(productId)).thenReturn(sampleProductResponse);


            when(orderRepositoryPort.save(any(Order.class))).thenAnswer(invocation -> {
                Order orderToSave = invocation.getArgument(0);
                return new Order(
                        orderId,
                        orderToSave.getUserId(),
                        orderToSave.getProductId(),
                        orderToSave.getQuantity(),
                        orderToSave.getTotalPrice(),
                        orderToSave.getStatus(),
                        orderToSave.getCreatedAt()
                );
            });


            Order result = orderApplicationService.createOrder(userId, productId, quantity);


            assertNotNull(result);
            assertEquals(OrderStatus.COMPLETED, result.getStatus());
            assertEquals(expectedTotal, result.getTotalPrice());


            verify(catalogClientPort, times(1)).getProduct(productId);
            verify(catalogClientPort, times(1)).reserveStock(productId, quantity);
            verify(userClientPort, times(1)).chargeWallet(userId, expectedTotal);
            verify(orderRepositoryPort, times(2)).save(orderCaptor.capture());


            verify(rabbitTemplate, times(1)).convertAndSend(
                    eq(RabbitMQConfig.EXCHANGE_NAME),
                    eq("order.created"),
                    eventCaptor.capture()
            );

            OrderCreatedEvent sentEvent = eventCaptor.getValue();
            assertEquals(orderId, sentEvent.orderId());
            assertEquals(userId, sentEvent.userId());
            assertEquals(expectedTotal, sentEvent.totalAmount());
        }

        @Test

        void shouldThrowExceptionWhenCatalogProductNotFoundTest() {

            when(catalogClientPort.getProduct(productId))
                    .thenThrow(new RuntimeException("Nie znaleziono produktu w katalogu"));


            assertThrows(RuntimeException.class, () ->
                    orderApplicationService.createOrder(userId, productId, 1)
            );

            verify(orderRepositoryPort, never()).save(any());
            verify(userClientPort, never()).chargeWallet(any(), any());
            verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
        }

        @Test

        void shouldCancelOrderWhenUserWalletChargeFails() {

            Integer quantity = 1;
            BigDecimal expectedTotal = new BigDecimal("100.00");

            when(catalogClientPort.getProduct(productId)).thenReturn(sampleProductResponse);

            Order pendingOrder = new Order(orderId, userId, productId, quantity, expectedTotal, OrderStatus.PENDING, null);
            when(orderRepositoryPort.save(any(Order.class))).thenReturn(pendingOrder);


            doNothing().when(catalogClientPort).reserveStock(productId, quantity);
            doThrow(new RuntimeException("Brak wystarczających środków na koncie"))
                    .when(userClientPort).chargeWallet(userId, expectedTotal);


            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    orderApplicationService.createOrder(userId, productId, quantity)
            );

            assertTrue(exception.getMessage().contains("Błąd podczas przetwarzania zamówienia"));


            verify(orderRepositoryPort, times(2)).save(orderCaptor.capture());

            Order lastSavedOrder = orderCaptor.getAllValues().get(1);
            assertEquals(OrderStatus.CANCELLED, lastSavedOrder.getStatus());


            verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
        }
    }

    @Nested
    @DisplayName("Testy pobierania zamówień")
    class GetOrderTests {

        @Test

        void shouldReturnOrderWhenExistsTest() {

            Order order = new Order(orderId, userId, productId, 1, new BigDecimal("100.00"), OrderStatus.COMPLETED, null);
            when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));


            Order result = orderApplicationService.getOrderById(orderId);


            assertNotNull(result);
            assertEquals(orderId, result.getId());
            verify(orderRepositoryPort, times(1)).findById(orderId);
        }

        @Test

        void shouldThrowExceptionWhenOrderNotFoundTest() {

            when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.empty());


            assertThrows(OrderNotFoundException.class, () ->
                    orderApplicationService.getOrderById(orderId)
            );
        }

        @Test

        void shouldReturnOrdersForUserTest() {

            Order order = new Order(orderId, userId, productId, 1, new BigDecimal("100.00"), OrderStatus.COMPLETED, null);
            when(orderRepositoryPort.findByUserId(userId)).thenReturn(List.of(order));


            List<Order> results = orderApplicationService.getOrdersByUserId(userId);


            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals(userId, results.get(0).getUserId());
            verify(orderRepositoryPort, times(1)).findByUserId(userId);
        }
    }
}

