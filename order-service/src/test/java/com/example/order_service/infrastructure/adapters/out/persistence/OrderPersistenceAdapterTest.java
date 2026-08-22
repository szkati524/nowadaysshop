package com.example.order_service.infrastructure.adapters.out.persistence;

import com.example.order_service.application.ports.out.CatalogClientPort;
import com.example.order_service.domain.model.Order;
import com.example.order_service.domain.model.OrderStatus;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@SpringBootTest

public class OrderPersistenceAdapterTest {


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry){
        registry.add("spring.cassandra.contact-points", () -> "localhost:9042");
        registry.add("spring.cassandra.local-datacenter", () -> "datacenter1");
        registry.add("spring.cassandra.keyspace-name", () -> "order_keyspace");
        registry.add("eureka.client.enabled", () -> "false");
    }
    @MockBean
    private CatalogClientPort catalogClientPort;
    @Autowired
    private OrderPersistenceAdapter orderPersistenceAdapter;

    @Test
    void shouldSaveAndRetrieveOrderFromCassandraTest(){
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Order order = new Order(
                UUID.randomUUID(),
                userId,
                productId,
                2,
                new BigDecimal("199.98"),
                OrderStatus.COMPLETED,
                Instant.now()
        );
        orderPersistenceAdapter.save(order);
        Optional<Order> retrieved = orderPersistenceAdapter.findById(order.getId());
        List<Order> userOrders = orderPersistenceAdapter.findByUserId(userId);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getUserId()).isEqualTo(userId);
        assertThat(retrieved.get().getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(userOrders).hasSize(1);
    }
}
