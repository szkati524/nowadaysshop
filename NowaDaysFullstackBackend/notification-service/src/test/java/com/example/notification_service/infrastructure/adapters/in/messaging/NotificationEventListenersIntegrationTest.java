package com.example.notification_service.infrastructure.adapters.in.messaging;

import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.notification_service.application.dto.OrderCreatedEvent;
import com.example.notification_service.application.dto.UserRegisteredEvent;
import com.example.notification_service.infrastructure.config.RabbitMQConsumerConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
@SpringBootTest
@Testcontainers
public class NotificationEventListenersIntegrationTest {
  @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.12-management");

    @DynamicPropertySource
    static void configureRabbit(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test

    void shouldConsumeUserRegisteredEventFromRabbit() {
        UserRegisteredEvent event = new UserRegisteredEvent(
                UUID.randomUUID(),
                "jan.kowalski@example.com",
                "Jan"
        );

        rabbitTemplate.convertAndSend("user.exchange", "user.registered", event);


        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {

        });
    }

    @Test
    @DisplayName("RabbitMQ Integration - Sukces: Odbiór zdarzenia order.created z exchange")
    void shouldConsumeOrderCreatedEventFromRabbit() {
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("150.00")
        );

        rabbitTemplate.convertAndSend("order.exchange", "order.created", event);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {

        });
    }
}

