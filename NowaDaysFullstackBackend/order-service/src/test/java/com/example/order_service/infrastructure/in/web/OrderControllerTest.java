package com.example.order_service.infrastructure.in.web;
import com.example.order_service.application.ports.in.OrderUseCase;
import com.example.order_service.domain.exception.GlobalExceptionHandler;
import com.example.order_service.domain.exception.OrderNotFoundException;
import com.example.order_service.domain.model.Order;
import com.example.order_service.domain.model.OrderStatus;
import com.example.order_service.infrastructure.adapters.in.web.dto.CreateOrderRequest;
import com.example.order_service.infrastructure.adapters.in.web.dto.OrderController;
import com.example.order_service.infrastructure.config.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderUseCase orderUseCase;

    @MockBean
    private JwtUtils jwtUtils;

    private final UUID sampleUserId = UUID.randomUUID();
    private final UUID sampleProductId = UUID.randomUUID();
    private final UUID sampleOrderId = UUID.randomUUID();

    @Nested
    @DisplayName("POST /api/orders - Tworzenie zamówienia")
    class CreateOrderEndpointTests {

        @Test

        void shouldReturn401WhenUserIsNotAuthenticatedTest() throws Exception {
            CreateOrderRequest request = new CreateOrderRequest(sampleProductId, 2);

            mockMvc.perform(post("/api/orders")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "00000000-0000-0000-0000-000000000001")

        void shouldReturn400WhenQuantityIsInvalidTest() throws Exception {
            CreateOrderRequest request = new CreateOrderRequest(sampleProductId, 0);

            mockMvc.perform(post("/api/orders")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "00000000-0000-0000-0000-000000000001")

        void shouldReturn400WhenProductIdIsNullTest() throws Exception {
            CreateOrderRequest request = new CreateOrderRequest(null, 2);

            mockMvc.perform(post("/api/orders")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")

        void shouldCreateOrderSuccessfullyTest() throws Exception {
            UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
            CreateOrderRequest request = new CreateOrderRequest(sampleProductId, 2);

            Order createdOrder = new Order(
                    sampleOrderId,
                    userId,
                    sampleProductId,
                    2,
                    new BigDecimal("200.00"),
                    OrderStatus.COMPLETED,
                    Instant.now()
            );

            when(orderUseCase.createOrder(eq(userId), eq(sampleProductId), eq(2)))
                    .thenReturn(createdOrder);

            mockMvc.perform(post("/api/orders")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(sampleOrderId.toString())))
                    .andExpect(jsonPath("$.userId", is(userId.toString())))
                    .andExpect(jsonPath("$.productId", is(sampleProductId.toString())))
                    .andExpect(jsonPath("$.quantity", is(2)))
                    .andExpect(jsonPath("$.totalPrice", is(200.00)))
                    .andExpect(jsonPath("$.status", is("COMPLETED")));
        }
    }

    @Nested
    @DisplayName("GET /api/orders/{id} - Pobieranie zamówienia po ID")
    class GetOrderByIdEndpointTests {

        @Test
        @WithMockUser

        void shouldReturnOrderWhenExistsTest() throws Exception {
            Order order = new Order(
                    sampleOrderId,
                    sampleUserId,
                    sampleProductId,
                    1,
                    new BigDecimal("100.00"),
                    OrderStatus.PENDING,
                    Instant.now()
            );

            when(orderUseCase.getOrderById(sampleOrderId)).thenReturn(order);

            mockMvc.perform(get("/api/orders/{id}", sampleOrderId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(sampleOrderId.toString())))
                    .andExpect(jsonPath("$.status", is("PENDING")));
        }

        @Test
        @WithMockUser

        void shouldReturn404WhenOrderNotFoundTest() throws Exception {
            when(orderUseCase.getOrderById(sampleOrderId))
                    .thenThrow(new OrderNotFoundException("Nie znaleziono zamówienia o ID: " + sampleOrderId));

            mockMvc.perform(get("/api/orders/{id}", sampleOrderId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/orders/user/{userId} - Pobieranie zamówień użytkownika")
    class GetOrdersByUserIdEndpointTests {

        @Test
        @WithMockUser

        void shouldReturnUserOrdersListTest() throws Exception {
            Order order = new Order(
                    sampleOrderId,
                    sampleUserId,
                    sampleProductId,
                    1,
                    new BigDecimal("100.00"),
                    OrderStatus.COMPLETED,
                    Instant.now()
            );

            when(orderUseCase.getOrdersByUserId(sampleUserId)).thenReturn(List.of(order));

            mockMvc.perform(get("/api/orders/user/{userId}", sampleUserId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].userId", is(sampleUserId.toString())));
        }
    }
}

