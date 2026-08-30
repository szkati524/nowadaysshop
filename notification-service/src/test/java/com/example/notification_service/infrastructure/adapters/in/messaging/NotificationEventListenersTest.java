package com.example.notification_service.infrastructure.adapters.in.messaging;
import com.example.notification_service.application.dto.OrderCreatedEvent;
import com.example.notification_service.application.dto.UserRegisteredEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
public class NotificationEventListenersTest {
    private NotificationEventListeners listener;

    @BeforeEach
    void setUp() {
        listener = new NotificationEventListeners();
    }

    @Test

    void shouldHandleUserRegisteredEventSuccessfully() {
        UserRegisteredEvent event = new UserRegisteredEvent(
                UUID.randomUUID(),
                "jan.kowalski@example.com",
                "Jan"
        );

        assertDoesNotThrow(() -> listener.handleUserRegistered(event));
    }

    @Test

    void shouldHandleOrderCreatedEventSuccessfully() {
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("299.99")
        );

        assertDoesNotThrow(() -> listener.handleOrderCreated(event));
    }
}


