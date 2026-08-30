package com.example.order_service.infrastructure.adapters.out.persistence;

import com.example.order_service.application.ports.out.CatalogClientPort;
import com.example.order_service.domain.model.Order;
import com.example.order_service.domain.model.OrderStatus;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.cassandra.DataCassandraTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DataCassandraTest
@Testcontainers
@Import(OrderPersistenceAdapter.class)
public class OrderPersistenceAdapterTest {
    @Container
    static CassandraContainer<?> cassandra = new CassandraContainer<>("cassandra:4.1");

    @Autowired
    private OrderPersistenceAdapter adapter;

    @Test

    void shouldSaveAndRetrieveFromCassandra() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Order order = new Order(
                orderId, userId, UUID.randomUUID(), 1, new BigDecimal("99.99"), OrderStatus.PENDING, Instant.now()
        );

        adapter.save(order);

        List<Order> userOrders = adapter.findByUserId(userId);

        assertThat(userOrders).hasSize(1);
        assertThat(userOrders.get(0).getId()).isEqualTo(orderId);
    }
}



